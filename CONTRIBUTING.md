## 🔄 Quy trình làm việc nhóm với GitHub (Git Team Workflow)

Để tránh xung đột mã nguồn (merge conflict), đảm bảo tính toàn vẹn của nhánh chính và giữ lịch sử commit sạch sẽ, toàn bộ thành viên bắt buộc tuân theo quy trình dưới đây.

---

### 1. Phân định file được phép và CẤM đưa vào Pull Request

| Nhóm file | Được phép đưa vào PR? | Quy tắc & Danh sách cụ thể |
| :--- | :---: | :--- |
| **Mã nguồn dự án** |  **CÓ** | Các file `.java` trong `controller`, `service`, `repository`, `dto`, `entity`,... |
| **Database Migration** |  **CÓ** | Các script mới trong `src/main/resources/db/migration/` (tuân thủ timestamp version, **không sửa file cũ đã merge**). |
| **Cấu hình & Build** |  **CÓ** | `build.gradle` (chỉ khi có thêm thư viện mới), `README.md`. |
| **File cấu hình cá nhân của IDE** | ❌ **CẤM** | `.idea/`, `*.iml`, `.vscode/`, `.settings/`, `.classpath`, `.project`. |
| **Thư mục build & file biên dịch** | ❌ **CẤM** | `build/`, `out/`, `target/`, `*.class`, `*.jar`. |
| **File môi trường / Thông tin nhạy cảm** | ❌ **CẤM** | Mật khẩu DB cá nhân, private key, JWT secret thực tế, file cấu hình mail riêng. |

> **Quy tắc vàng:** Luôn kiểm tra lệnh `git status` trước khi add để đảm bảo không vô tình commit các file rác IDE hoặc file nhạy cảm.

---

### 2. Quy ước đặt tên nhánh (Branch Naming Convention)
**Nhánh main là nhánh để deploy.**
Nếu tạo pull request thì chỉ dc pull vào nhánh test tuyệt đối ko trỏ vào main

Mọi tính năng mới hoặc sửa lỗi đều phải được làm việc trên nhánh riêng, tuyệt đối **không code trực tiếp trên nhánh `main`**.

* **Tính năng mới:** `feature/<tên-tính-năng>` *(Ví dụ: `feature/user-auth`, `feature/course-crud`)*
* **Sửa lỗi:** `bugfix/<lỗi-cần-sửa>` *(Ví dụ: `bugfix/fix-null-pointer-login`)*
* **Cấu hình / Tối ưu:** `chore/<nội-dung>` hoặc `refactor/<nội-dung>` *(Ví dụ: `chore/add-lombok`, `refactor/api-response`)*

---

### 3. Các bước thực hiện luồng công việc (Step-by-Step Commands)

#### Bước 1: Cập nhật code mới nhất từ nhánh `main`
Trước khi bắt tay vào làm việc hoặc trước khi tạo nhánh mới, luôn kéo code mới nhất từ repo về:
```bash
git checkout test
git pull origin test
```
Bước 2: Tạo nhánh làm việc mới từ mainBashgit 
```code
checkout -b feature/auth-service
```
Bước 3: Code, kiểm thử và commit cục bộThực hiện viết code và test cục bộ chạy bình thường. Commit theo từng module tính năng nhỏ:Bash# Xem các file vừa thay đổi
```code
git status
```
# Đưa file vào staging area (chỉ add đúng file liên quan)
```code
git add src/main/java/com/example/ilms/service/AuthService.java
```
# Commit với thông điệp rõ ràng
```code
git commit -m "feat: implement user registration with password encryption"
```
Bước 4: Đồng bộ lại main trước khi đẩy code (Tránh xung đột)Trong lúc bạn code, có thể người khác đã merge code mới vào main. Hãy kéo cập nhật mới nhất vào nhánh của bạn trước khi push:Bashgit fetch origin
```code
git merge origin/main
```
(Nếu xảy ra conflict, mở file bị xung đột lên resolve, sau đó git add và git commit để hoàn tất).Bước 5: Đẩy nhánh lên GitHubBashgit 
```code
push -u origin feature/auth-service
```
# 4. Quy trình tạo và duyệt Pull Request (PR Review)
1.Mở Pull Request:Trên giao diện web.Truy cập GitHub > chọn tab Pull requests > New pull request:Base: main (hoặc nhánh kiểm thử test).Compare: feature/auth-service (nhánh của bạn).

2.Điền thông tin PR:Rõ ràng & Ngắn gọn.Đặt tiêu đề theo chuẩn: 
[Feature] Tích hợp đăng ký tài khoản qua Email.Mô tả: Nêu rõ những việc đã làm, các endpoint mới tạo, và lưu ý (nếu có script migration DB mới).Gắn Reviewer: Chọn Leader hoặc đồng đội phụ trách review code.Bấm nút Create pull request.

3.Quá trình Code Review:Trao đổi kỹ thuật.Reviewer sẽ vào tab Files changed để xem từng dòng code.Nếu có đoạn code chưa tối ưu: Reviewer comment yêu cầu sửa đổi (Request changes).Developer sửa lại code tại nhánh ở local, commit và push lên lại (PR trên GitHub sẽ tự động cập nhật commit mới).

4.Approve & Merge:Người được chỉ định.Khi code đã chuẩn: Reviewer bấm Approve.Người có quyền (Leader / Maintainer trong Bypass list) bấm nút Squash and merge hoặc Merge pull request.Xóa nhánh remote vừa merge để giữ repo gọn gàng.

5.Dọn dẹp sau khi Merge thành côngSau khi PR đã được merge vào main trên GitHub, thành viên xóa nhánh làm việc cũ ở máy cá nhân để chuẩn bị cho task tiếp theo:Bash# Chuyển về lại nhánh main
```code
git checkout main
```   

## Lấy code mới nhất vừa được merge về máy
```code
git pull origin main
```
## Xóa nhánh tính năng cũ ở máy cục bộ
```code
git branch -d feature/auth-service
```