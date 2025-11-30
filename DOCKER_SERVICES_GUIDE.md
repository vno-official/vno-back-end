# Docker Services Manager - Interactive Script

## 🎯 Tổng quan

`docker-services.bat` là script tương tác để quản lý tất cả VNO Docker services từ một menu duy nhất.

## 🚀 Cách sử dụng

Chỉ cần chạy:
```cmd
docker-services.bat
```

## 📋 Menu chính

```
========================================
   VNO Docker Services Manager
========================================

1. Start a service
2. Stop a service
3. View running containers
4. View logs
5. Exit
```

## 🔧 Chức năng

### 1. Start a Service

Cho phép bạn chọn service để khởi động:

```
Available services:

1. Auth Service (Port 8080)
2. User Service (Port 8081)
3. Note Service (Port 8082)
4. Realtime Collab Service (Port 8083)
5. Notification Producer (Port 8084)
6. Notification Processor (Port 8085)
7. All Services
8. Back to main menu
```

**Tính năng**:
- ✅ Tự động map port đúng
- ✅ Tự động xóa container cũ nếu tồn tại
- ✅ Chạy ở detached mode (background)
- ✅ Hiển thị URL để access service
- ✅ Hướng dẫn xem logs

### 2. Stop a Service

Cho phép bạn chọn service để dừng:

```
Running services:
  - vno-auth-service-container
  - vno-user-service-container

Available services to stop:

1. Auth Service
2. User Service
3. Note Service
4. Realtime Collab Service
5. Notification Producer
6. Notification Processor
7. All Services
8. Back to main menu
```

**Tính năng**:
- ✅ Hiển thị services đang chạy
- ✅ Dừng và xóa container
- ✅ Thông báo nếu service không chạy

### 3. View Running Containers

Hiển thị tất cả VNO containers đang chạy:

```
NAMES                              STATUS              PORTS
vno-auth-service-container         Up 5 minutes        0.0.0.0:8080->8080/tcp
vno-user-service-container         Up 3 minutes        0.0.0.0:8081->8081/tcp
```

### 4. View Logs

Cho phép xem logs của service đang chạy:

```
Running services:

1. vno-auth-service-container
2. vno-user-service-container
3. Back to main menu

Choose a service to view logs (1-3):
```

**Tính năng**:
- ✅ Chỉ hiển thị services đang chạy
- ✅ Follow logs real-time (Ctrl+C để thoát)
- ✅ Quay lại menu sau khi xem logs

## 💡 Ví dụ sử dụng

### Scenario 1: Start một service để test

```
1. Chạy docker-services.bat
2. Chọn "1" (Start a service)
3. Chọn "1" (Auth Service)
4. Service khởi động ở background
5. Access tại http://localhost:8080
```

### Scenario 2: Start tất cả services

```
1. Chạy docker-services.bat
2. Chọn "1" (Start a service)
3. Chọn "7" (All Services)
4. Tất cả services khởi động
```

### Scenario 3: Xem logs của một service

```
1. Chạy docker-services.bat
2. Chọn "4" (View logs)
3. Chọn service muốn xem
4. Logs hiển thị real-time
5. Ctrl+C để quay lại menu
```

### Scenario 4: Stop một service

```
1. Chạy docker-services.bat
2. Chọn "2" (Stop a service)
3. Xem danh sách services đang chạy
4. Chọn service muốn stop
5. Service dừng và container bị xóa
```

## 🎨 Ưu điểm

✅ **Tất cả trong một**: Không cần nhớ nhiều scripts  
✅ **Interactive**: Menu rõ ràng, dễ sử dụng  
✅ **An toàn**: Hiển thị status trước khi thực hiện  
✅ **Thông minh**: Tự động xóa container cũ  
✅ **Tiện lợi**: Xem logs ngay trong script  
✅ **Không cần nhớ port**: Script tự động map đúng port

## 🆚 So sánh với các scripts khác

| Script | Use Case | Ưu điểm | Nhược điểm |
|--------|----------|---------|------------|
| `docker-services.bat` | Quản lý tổng thể | Interactive, đầy đủ tính năng | Cần tương tác |
| `run-all-services.bat` | Start nhanh tất cả | Một lệnh, không cần tương tác | Không có menu |
| `run-auth-service.bat` | Start một service | Đơn giản, nhanh | Cần nhiều files |
| `docker-compose` | Production deployment | Quản lý phức tạp | Cần config file |

## 🔍 Troubleshooting

### Script không chạy được

```cmd
REM Kiểm tra Docker đang chạy
docker info

REM Nếu Docker không chạy, start Docker Desktop
```

### Image không tồn tại

Nếu thấy lỗi khi start service:
```
[ERROR] Failed to start auth-service

Make sure the Docker image exists. Build it with:
  gradlew :auth-service:build
  docker build -f auth-service/src/main/docker/Dockerfile.jvm -t vno-auth-service:latest auth-service
```

Build image trước:
```cmd
REM Build tất cả images
build-all-images.bat

REM Hoặc build từng service
gradlew :auth-service:build
docker build -f auth-service/src/main/docker/Dockerfile.jvm -t vno-auth-service:latest auth-service
```

### Container đã tồn tại

Script tự động xóa container cũ, nhưng nếu gặp vấn đề:
```cmd
REM Xóa container thủ công
docker rm -f vno-auth-service-container

REM Hoặc xóa tất cả VNO containers
docker rm -f $(docker ps -aq --filter "name=vno-")
```

## 📝 Lưu ý

- Script chạy containers ở **detached mode** (background)
- Containers tự động bị xóa khi stop (không dùng `--rm` vì detached)
- Port mapping tự động theo chuẩn: 8080-8085
- Container names có format: `vno-{service-name}-container`

## 🎓 Tips

1. **Xem tất cả containers**: Chọn option 3 trong menu
2. **Xem logs real-time**: Chọn option 4 và chọn service
3. **Stop nhanh tất cả**: Chọn option 2, sau đó chọn "All Services"
4. **Kết hợp với Kong**: Start services rồi start Kong Gateway

## 🚀 Workflow khuyến nghị

### Development
```
1. docker-services.bat
2. Chọn "1" → "7" (Start all services)
3. cd gateway && docker-compose up -d (Start Kong)
4. Test qua Kong: http://localhost:8000/api/auth
```

### Testing một service
```
1. docker-services.bat
2. Chọn "1" → Chọn service cần test
3. Chọn "4" → Xem logs của service đó
4. Test service
5. Chọn "2" → Stop service
```
