# Docker Services Manager - Quick Start

## 🎯 Cách dùng nhanh nhất

Chỉ cần chạy:
```cmd
docker-services.bat
```

## 📸 Demo

### Menu chính
```
========================================
   VNO Docker Services Manager
========================================

1. Start a service
2. Stop a service
3. View running containers
4. View logs
5. Exit

Choose an action (1-5): _
```

### Start service
```
Choose an action (1-5): 1

========================================
   Start a Service
========================================

Available services:

1. Auth Service (Port 8080)
2. User Service (Port 8081)
3. Note Service (Port 8082)
4. Realtime Collab Service (Port 8083)
5. Notification Producer (Port 8084)
6. Notification Processor (Port 8085)
7. All Services
8. Back to main menu

Choose a service to start (1-8): 1

Starting auth-service on port 8080...

[OK] auth-service started successfully on port 8080

Access at: http://localhost:8080
View logs: docker logs -f vno-auth-service-container

Press any key to continue...
```

### Stop service
```
Choose an action (1-5): 2

========================================
   Stop a Service
========================================

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

Choose a service to stop (1-8): 1

Stopping auth-service...
[OK] Stopped vno-auth-service-container
[OK] Removed vno-auth-service-container

Press any key to continue...
```

## ⚡ Quick Commands

| Muốn làm gì? | Làm sao? |
|--------------|----------|
| Start 1 service | `docker-services.bat` → 1 → chọn service |
| Start tất cả | `docker-services.bat` → 1 → 7 |
| Stop 1 service | `docker-services.bat` → 2 → chọn service |
| Stop tất cả | `docker-services.bat` → 2 → 7 |
| Xem đang chạy gì | `docker-services.bat` → 3 |
| Xem logs | `docker-services.bat` → 4 → chọn service |

## 🎁 Tính năng hay

✅ **Không cần nhớ port** - Script tự động map đúng port  
✅ **Không cần nhớ tên container** - Script tự động đặt tên  
✅ **Xem logs ngay** - Không cần gõ lệnh docker logs  
✅ **An toàn** - Tự động xóa container cũ trước khi start  
✅ **Tiện lợi** - Tất cả trong một script

## 📚 Xem thêm

- [DOCKER_SERVICES_GUIDE.md](DOCKER_SERVICES_GUIDE.md) - Hướng dẫn chi tiết
- [DOCKER_RUN_SCRIPTS.md](DOCKER_RUN_SCRIPTS.md) - Giải thích về Docker port mapping
- [QUICKSTART.md](QUICKSTART.md) - Quick reference tổng hợp
