# Docker Setup Guide

Bu proje Docker Compose kullanarak backend, frontend ve PostgreSQL'i ayağa kaldırır.

## Gereksinimler

- Docker
- Docker Compose

## Hızlı Başlangıç

### 1. Tüm servisleri başlat

```bash
docker-compose up -d
```

### 2. Servisleri durdur

```bash
docker-compose down
```

### 3. Logları görüntüle

```bash
docker-compose logs -f
```

### 4. Belirli bir servisin loglarını görüntüle

```bash
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

## Servisler

### PostgreSQL
- **Port**: 5432
- **Database**: tshirt_satis
- **Username**: postgres
- **Password**: tata
- **Volume**: postgres_data (kalıcı veri saklama)

### Backend (Spring Boot)
- **Port**: 8080
- **URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Uploads**: `./backend/uploads` klasörü volume olarak mount edilir

### Frontend (Vite/React)
- **Port**: 3001
- **URL**: http://localhost:3001
- **Build**: Production build (nginx ile serve edilir)

## Environment Variables

### Backend
Backend environment variable'ları `docker-compose.yml` dosyasında tanımlanmıştır:
- `SPRING_DATASOURCE_URL`: PostgreSQL bağlantı URL'i
- `SPRING_DATASOURCE_USERNAME`: PostgreSQL kullanıcı adı
- `SPRING_DATASOURCE_PASSWORD`: PostgreSQL şifresi
- `SECURITY_JWT_SECRET_KEY`: JWT secret key
- `SECURITY_JWT_EXPIRATION_TIME`: JWT expiration time

### Frontend
Frontend environment variable'ları:
- `VITE_API_URL`: Backend API URL'i (default: http://localhost:8080)

## Veritabanı İşlemleri

### Veritabanını sıfırla

```bash
docker-compose down -v
docker-compose up -d
```

### Veritabanına bağlan

```bash
docker exec -it tshirt-postgres psql -U postgres -d tshirt_satis
```

## Geliştirme Modu

### Backend'i yeniden build et

```bash
docker-compose build backend
docker-compose up -d backend
```

### Frontend'i yeniden build et

```bash
docker-compose build frontend
docker-compose up -d frontend
```

## Sorun Giderme

### Servisler başlamıyor

1. Portların kullanılabilir olduğundan emin olun:
   - 8080 (Backend)
   - 3001 (Frontend)
   - 5432 (PostgreSQL)

2. Logları kontrol edin:
   ```bash
   docker-compose logs
   ```

### Backend PostgreSQL'e bağlanamıyor

1. PostgreSQL'in sağlıklı olduğundan emin olun:
   ```bash
   docker-compose ps
   ```

2. Backend loglarını kontrol edin:
   ```bash
   docker-compose logs backend
   ```

### Frontend API'ye bağlanamıyor

1. Backend'in çalıştığından emin olun
2. `VITE_API_URL` environment variable'ının doğru olduğundan emin olun
3. Browser console'da CORS hatalarını kontrol edin

## Production Deployment

Production için:
1. Environment variable'ları güvenli bir şekilde yönetin
2. SSL/TLS sertifikaları ekleyin
3. Database şifrelerini güçlendirin
4. Resource limitleri ayarlayın
5. Monitoring ve logging ekleyin

## Komutlar Özeti

```bash
# Başlat
docker-compose up -d

# Durdur
docker-compose down

# Yeniden başlat
docker-compose restart

# Loglar
docker-compose logs -f

# Servis durumu
docker-compose ps

# Yeniden build
docker-compose build

# Temizle ve başlat
docker-compose down -v && docker-compose up -d
```

