package com.example.ilms.service;
// mở kết nối SSE, gửi sự kiện khi có thông báo mới, và dọn kết nối khi kết thúc.
import com.example.ilms.entity.User;
import com.example.ilms.event.AnnouncementCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j // là annotation của Lombok, dùng để tự tạo biến log cho class,  ghi lại thông tin khi chương trình chạy
@Service
@RequiredArgsConstructor // Lombok tạo constructor cho các field final chưa được khởi tạo
public class AnnouncementSseService {
    private final AnnouncementAccessService announcementAccessService;

    private record Connection(
            UUID classId,
            UUID userId,
            SseEmitter emitter // là class của Spring MVC, dùng để server liên tục gửi sự kiện xuống client thông qua một kết nối HTTP được giữ mở
    ){}

    private final Map<UUID, Connection> connections = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID classId, User user) { // Hàm này được gọi khi người dùng muốn nhận thông báo trực tiếp của một lớp.
        announcementAccessService.getActiveMember(classId, user);

        UUID connectionId = UUID.randomUUID(); // mã kết nối
        SseEmitter emitter = new SseEmitter(60_000L); // tạo 1 luồng phản hồi sse với timeout = 60s
        connections.put(
                connectionId,
                new Connection(classId, user.getId(), emitter)
        );
        // khi kết nối đã kết thúc
        emitter.onCompletion(()
                -> connections.remove(connectionId));
        // đăng ký hành động sẽ chạy khi kết nối hết thời gian chờ
        emitter.onTimeout(()
                -> connections.remove(connectionId));
        // khi kết nối gặp lỗi
        emitter.onError((e)
                -> connections.remove(connectionId));

        try {
            // tạo đối tượng xây dựng sự kiện
            emitter.send(
                    SseEmitter.event().name("connected").data(Map.of("classId", classId))
            );
        } catch (IOException | RuntimeException e) {
            connections.remove(connectionId);
        }
        return emitter;
    }


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onAnnouncementCreated(AnnouncementCreateEvent event) {
        connections.forEach((connectionId, connection) -> {
            // Chỉ gửi tới kết nối đang nghe đúng lớp.
            if (!connection.classId().equals(event.classId())) {
                return;
            }

            try {
                // Kiểm tra lại vì người dùng có thể đã bị xoá khỏi lớp.
                boolean canReceive = announcementAccessService.isActiveMember(
                        event.classId(),
                        connection.userId()
                );

                if (!canReceive) {
                    disconnect(connectionId);
                    return;
                }

                connection.emitter().send(
                        SseEmitter.event()
                                .name("announcement-created")
                                .data(event)
                );
            } catch (IOException | RuntimeException e) {
                log.debug(
                        "Không gửi được SSE tới kết nối {}",
                        connectionId,
                        e
                );

                disconnect(connectionId);
            }
        });
    }

    private void disconnect(UUID connectionId) {
        Connection connection = connections.remove(connectionId);

        if (connection != null) {
            try {
                connection.emitter().complete();
            } catch (RuntimeException e) {
                log.debug("Kết nối SSE đã đóng: {}", connectionId);
            }
        }
    }

}
