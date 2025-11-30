# Docker Run Scripts - Quick Reference

## 📝 Giải thích

Docker **không thể** tự động publish ports chỉ với `EXPOSE` trong Dockerfile. Bạn vẫn cần dùng `-p` hoặc `-P` khi chạy container.

**Lý do**: `EXPOSE` chỉ là metadata để document port nào được sử dụng, nhưng không tự động map port ra host machine.

## 🚀 Giải pháp

Tôi đã tạo các wrapper scripts để bạn không cần nhớ port của từng service:

### Chạy từng service riêng lẻ

```cmd
REM Chỉ cần gõ tên script, port tự động được map
run-auth-service.bat           # Port 8080
run-user-service.bat           # Port 8081
run-note-service.bat           # Port 8082
run-realtime-collab-service.bat # Port 8083
run-notification-producer.bat   # Port 8084
run-notification-processor.bat  # Port 8085
```

### Chạy tất cả services cùng lúc

```cmd
REM Chạy tất cả services trong detached mode
run-all-services.bat

REM Dừng tất cả services
stop-all-services.bat
```

## 📋 Chi tiết các scripts

### Individual Service Scripts

Mỗi script (`run-auth-service.bat`, `run-user-service.bat`, etc.) sẽ:
- Tự động map port đúng (8080, 8081, 8082, ...)
- Chạy container với `--rm` (tự động xóa khi dừng)
- Đặt tên container rõ ràng (`vno-auth-service-container`)
- Chạy ở foreground mode (xem logs trực tiếp)

**Ví dụ**:
```cmd
run-auth-service.bat
# Tương đương: docker run --rm -p 8080:8080 --name vno-auth-service-container vno-auth-service:latest
```

### Batch Scripts

**`run-all-services.bat`**:
- Chạy tất cả 6 services cùng lúc
- Detached mode (chạy background)
- Tự động xóa container cũ nếu tồn tại
- Hiển thị status của từng service

**`stop-all-services.bat`**:
- Dừng tất cả VNO service containers
- Xóa containers sau khi dừng
- Hiển thị status

## 🎯 Cách sử dụng

### Scenario 1: Test một service
```cmd
REM Chạy auth service để test
run-auth-service.bat

REM Service chạy ở foreground, bạn thấy logs
REM Ctrl+C để dừng
```

### Scenario 2: Chạy tất cả services
```cmd
REM Chạy tất cả services
run-all-services.bat

REM Xem logs của một service
docker logs -f vno-auth-service-container

REM Dừng tất cả
stop-all-services.bat
```

### Scenario 3: Kết hợp với Kong Gateway
```cmd
REM 1. Chạy tất cả services
run-all-services.bat

REM 2. Chạy Kong (services tự động register)
cd gateway
docker-compose up -d

REM 3. Test qua Kong
curl http://localhost:8000/api/auth/health
```

## 🔍 Các lệnh hữu ích

```cmd
REM Xem containers đang chạy
docker ps

REM Xem logs của một service
docker logs vno-auth-service-container
docker logs -f vno-user-service-container  # Follow mode

REM Dừng một service cụ thể
docker stop vno-auth-service-container

REM Vào trong container
docker exec -it vno-auth-service-container sh

REM Xem resource usage
docker stats
```

## ⚠️ Lưu ý quan trọng

### Về EXPOSE trong Dockerfile

`EXPOSE` trong Dockerfile **KHÔNG** tự động publish ports. Nó chỉ:
1. Document port nào được sử dụng
2. Cho phép container-to-container communication trong cùng network
3. Được sử dụng khi dùng `-P` (uppercase) để auto-map

### Nếu muốn dùng -P

Bạn có thể dùng `-P` để Docker tự động map exposed ports sang random ports:

```cmd
docker run -P vno-auth-service:latest
# Docker sẽ map 8080 -> random port (ví dụ: 32768)

# Xem port nào được map
docker ps
```

Nhưng cách này **không tiện** vì port thay đổi mỗi lần chạy.

### Tại sao cần wrapper scripts?

Docker không có cơ chế "default port mapping" trong image. Bạn **bắt buộc** phải chỉ định port khi chạy container.

Wrapper scripts giúp:
- ✅ Không cần nhớ port của từng service
- ✅ Consistent naming cho containers
- ✅ Dễ dàng quản lý (start/stop all)
- ✅ Tự động cleanup với `--rm`

## 🆚 So sánh các cách chạy

| Cách | Lệnh | Ưu điểm | Nhược điểm |
|------|------|---------|------------|
| Manual | `docker run -p 8080:8080 vno-auth-service` | Linh hoạt | Phải nhớ port |
| Auto-map | `docker run -P vno-auth-service` | Không cần chỉ định port | Port random, khó nhớ |
| Wrapper script | `run-auth-service.bat` | Đơn giản, port cố định | Cần script riêng |
| Docker Compose | `docker-compose up` | Quản lý nhiều services | Cần file config |

## 🎓 Khuyến nghị

**Cho development**:
- Dùng `dev-start.bat` để chạy services locally (không dùng Docker)
- Hoặc dùng `run-all-services.bat` nếu muốn test trong Docker

**Cho testing**:
- Dùng individual scripts (`run-auth-service.bat`) để test từng service
- Dùng `run-all-services.bat` + Kong để test integration

**Cho production**:
- Dùng `docker-compose -f gateway/docker-compose-services.yml up -d`
- Hoặc deploy lên Kubernetes/cloud platform
