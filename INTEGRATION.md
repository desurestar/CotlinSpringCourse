# Инструкция по интеграции Frontend и Backend

## Предварительные требования

- Java 17+
- Maven 3.6+
- Android Studio (последняя версия)
- Android SDK (API 24+)

## Шаг 1: Запуск Backend

1. Откройте терминал в корне проекта
2. Перейдите в папку Backend:
   ```bash
   cd Back
   ```
3. Запустите Spring Boot приложение:
   ```bash
   ./mvnw spring-boot:run
   ```
   или на Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```
4. Дождитесь сообщения: `Started Lab2corpApplication`
5. Backend будет доступен на `http://localhost:8080`

## Шаг 2: Проверка Backend API

Проверьте что API работает:

```bash
# Проверка departments
curl http://localhost:8080/api/departments

# Проверка login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@institute.ru","password":"password123"}'
```

Ожидаемый ответ login:
```json
{
  "token": "...",
  "userId": 7,
  "email": "admin@institute.ru",
  "role": "ADMIN"
}
```

## Шаг 3: Настройка Android приложения

### Для Android Emulator

1. Откройте `Front/app/src/main/java/com/example/front/util/Constants.kt`
2. Убедитесь что BASE_URL установлен:
   ```kotlin
   const val BASE_URL = "http://10.0.2.2:8080/"
   ```

### Для реального Android устройства

1. Узнайте IP адрес вашего компьютера:
   - **Windows**: откройте CMD и выполните `ipconfig` → найдите IPv4 Address
   - **Mac/Linux**: выполните `ifconfig` или `ip addr` → найдите inet
   
2. Обновите `Constants.kt`:
   ```kotlin
   const val BASE_URL = "http://192.168.X.XXX:8080/"  // замените на ваш IP
   ```

3. Убедитесь что:
   - Компьютер и устройство в одной WiFi сети
   - Firewall не блокирует порт 8080

## Шаг 4: Запуск Android приложения

1. Откройте проект `Front/` в Android Studio
2. Нажмите "Sync Project with Gradle Files" (иконка слона)
3. Дождитесь завершения синхронизации
4. Запустите эмулятор или подключите реальное устройство
5. Нажмите "Run" (зеленая стрелка) или Shift+F10

## Шаг 5: Тестирование

### Тестовые учетные данные

Из `Back/src/main/resources/data.sql`:

| Email | Password | Role |
|-------|----------|------|
| admin@institute.ru | password123 | ADMIN |
| manager@institute.ru | password123 | EMPLOYEE |
| employee@institute.ru | password123 | EMPLOYEE |
| student1@institute.ru | password123 | STUDENT |

### Сценарий тестирования

1. **Запустите приложение**
   - Должен открыться экран Login

2. **Войдите в систему**
   - Email: `admin@institute.ru`
   - Password: `password123`
   - Нажмите "Войти"

3. **Просмотр отделов**
   - После входа откроется список отделов
   - Должно отображаться 5 отделов: PIN, IS, PMI, IB, RT

4. **Поиск отделов**
   - Введите "PIN" в поле поиска
   - Должен остаться только Department PIN

5. **Просмотр деталей отдела**
   - Нажмите на любой отдел
   - Откроется список сотрудников этого отдела

6. **Просмотр профиля сотрудника**
   - Нажмите на любого сотрудника
   - Откроется профиль с полной информацией

## Проверка логов

### Backend logs (Spring Boot Console)

Успешный запрос:
```
o.s.web.servlet.DispatcherServlet : Completed 200 OK
```

### Android logs (Logcat в Android Studio)

Фильтр: `OkHttp`

Успешный запрос:
```
D/OkHttp: --> POST http://10.0.2.2:8080/api/auth/login
D/OkHttp: <-- 200 OK (234ms)
```

## Troubleshooting

### Проблема: "Unable to resolve host"

**Причина**: Backend не запущен или неверный URL

**Решение**:
1. Убедитесь что Backend запущен на порту 8080
2. Проверьте BASE_URL в Constants.kt
3. Для эмулятора используйте `10.0.2.2`, а не `localhost`

### Проблема: "HTTP 401 Unauthorized"

**Причина**: Неверные учетные данные или проблема с SecurityConfig

**Решение**:
1. Проверьте email и пароль (регистр важен!)
2. Убедитесь что пользователь существует в БД (см. data.sql)
3. Проверьте что `/api/auth/login` открыт в SecurityConfig

### Проблема: "HTTP 403 Forbidden"

**Причина**: CORS не настроен или CSRF блокирует запрос

**Решение**:
1. Убедитесь что CorsConfig создан и активен
2. Проверьте что CSRF отключен для `/api/**` в SecurityConfig

### Проблема: "Connection refused"

**Причина**: Backend не слушает на нужном порту

**Решение**:
1. Убедитесь что Backend запущен
2. Проверьте в логах строку: `Tomcat started on port(s): 8080`
3. Проверьте что порт 8080 не занят другим приложением

### Проблема: Пустые списки в Android

**Причина**: Данные не загружены в БД

**Решение**:
1. Проверьте что `data.sql` выполнился при старте Backend
2. Проверьте логи Backend на наличие SQL ошибок
3. Попробуйте запрос напрямую: `curl http://localhost:8080/api/departments`

## API Endpoints

### Authentication
- `POST /api/auth/login` - вход в систему
- `POST /api/auth/register` - регистрация
- `GET /api/auth/me` - текущий пользователь

### Departments
- `GET /api/departments` - список отделов (query: ?search=text)
- `GET /api/departments/{id}` - детали отдела
- `POST /api/departments` - создать отдел (ADMIN/EMPLOYEE)
- `PUT /api/departments/{id}` - обновить отдел (ADMIN/EMPLOYEE)
- `DELETE /api/departments/{id}` - удалить отдел (ADMIN)

### Employees
- `GET /api/employees` - список сотрудников (query: ?search=text&departmentId=1)
- `GET /api/employees/{id}` - детали сотрудника
- `POST /api/employees` - создать сотрудника (ADMIN/EMPLOYEE)
- `PUT /api/employees/{id}` - обновить сотрудника (ADMIN/EMPLOYEE)
- `DELETE /api/employees/{id}` - удалить сотрудника (ADMIN)

### Posts
- `GET /api/posts` - список должностей
- `GET /api/posts/{id}` - детали должности
- `POST /api/posts` - создать должность (ADMIN/EMPLOYEE)
- `PUT /api/posts/{id}` - обновить должность (ADMIN/EMPLOYEE)
- `DELETE /api/posts/{id}` - удалить должность (ADMIN)

## Полезные команды

### Остановить Backend
```bash
# В терминале где запущен Backend
Ctrl + C
```

### Пересобрать Backend
```bash
cd Back
./mvnw clean install
./mvnw spring-boot:run
```

### Очистить и пересобрать Android
В Android Studio:
1. Build → Clean Project
2. Build → Rebuild Project

### Посмотреть БД H2 (для отладки)
1. Добавьте в `application.properties`:
   ```properties
   spring.h2.console.enabled=true
   ```
2. Откройте в браузере: `http://localhost:8080/h2-console`
3. JDBC URL: `jdbc:h2:mem:testdb`
4. User: `sa`, Password: (пусто)

## Архитектура интеграции

### Backend (Spring Boot)
- **REST API контроллеры**: Обрабатывают HTTP запросы
- **Security**: Управляет аутентификацией и авторизацией
- **CORS**: Разрешает cross-origin запросы от Android
- **H2 Database**: In-memory база данных с тестовыми данными

### Frontend (Android)
- **Retrofit**: HTTP клиент для REST API запросов
- **ViewModel**: Управление состоянием UI
- **Repository**: Абстракция для работы с данными
- **SharedPreferences**: Хранение токена авторизации

### Поток данных
1. Пользователь вводит логин/пароль
2. Android отправляет POST запрос на `/api/auth/login`
3. Backend проверяет credentials
4. Backend возвращает JWT токен
5. Android сохраняет токен в SharedPreferences
6. Все последующие запросы включают токен в заголовке Authorization
7. Backend проверяет токен и возвращает данные

## Безопасность

### Текущая реализация (для разработки)
- Пароли хранятся в открытом виде
- JWT токен простой (email + timestamp в Base64)
- CORS разрешает все origins (`*`)

### Для продакшена (TODO)
- [ ] Использовать BCryptPasswordEncoder для хранения паролей
- [ ] Реализовать полноценный JWT с подписью HMAC
- [ ] Ограничить CORS конкретными origins
- [ ] Добавить rate limiting для API
- [ ] Использовать HTTPS для всех запросов
- [ ] Добавить refresh tokens
- [ ] Реализовать token blacklist при logout

## Известные ограничения

1. **Аутентификация**: Упрощенная реализация JWT для учебных целей
2. **База данных**: H2 in-memory - данные теряются при перезапуске
3. **Безопасность**: Нет защиты от brute-force атак
4. **Валидация**: Минимальная валидация входных данных
5. **Обработка ошибок**: Базовая, можно улучшить

## Дальнейшее развитие

### Возможные улучшения:
1. Миграция на PostgreSQL/MySQL
2. Добавление пагинации для больших списков
3. Реализация фильтров и сортировки
4. Добавление file upload для аватаров
5. Websocket для real-time updates
6. Кэширование на стороне Android
7. Offline режим с синхронизацией
8. Push уведомления
9. Comprehensive unit и integration тесты
10. API документация с Swagger/OpenAPI

## Контакты и поддержка

При возникновении проблем:
1. Проверьте секцию Troubleshooting
2. Изучите логи Backend и Android
3. Проверьте версии Java, Maven, Android SDK
4. Убедитесь что все порты свободны
5. Перезапустите Backend и Android приложение
