# QuizApp - Interactive Quiz Platform

A comprehensive web-based quiz application built with Spring Boot that allows teachers to create and manage quizzes while students can participate and track their progress.

## 🚀 Features

### For Teachers
- ✅ Create and manage quizzes with multiple-choice questions
- ✅ Publish/Unpublish quizzes with unique join codes
- ✅ View quiz statistics and analytics
- ✅ Track student performance and results
- ✅ Access quiz-specific leaderboards
- ✅ Real-time dashboard with quiz status

### For Students
- ✅ Join quizzes using unique codes
- ✅ Take quizzes with timed questions
- ✅ View results and scores
- ✅ Access global and quiz-specific leaderboards
- ✅ Track quiz history and progress

### Security Features
- ✅ JWT-based authentication
- ✅ Spring Security integration
- ✅ Role-based access control (Teacher/Student)
- ✅ Password encryption with BCrypt
- ✅ CSRF protection

## 🛠️ Technology Stack

- **Backend:** Java 17, Spring Boot 3.2.0
- **Frontend:** Thymeleaf, Bootstrap 5, Font Awesome
- **Database:** PostgreSQL (Production), H2 (Testing)
- **Security:** Spring Security, JWT
- **Build Tool:** Maven
- **ORM:** Hibernate/JPA
- **Validation:** Jakarta Bean Validation

## 📋 Prerequisites

Before running this application, ensure you have:

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher (or use the included Maven wrapper)

## 🔧 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Jashdotcom/QuizApp.git
cd QuizApp
```

### 2. Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE quizapp;
```

Update the database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/quizapp
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Configure JWT Secret

Update the JWT secret in `application.properties`:

```properties
quizapp.jwtSecret=your_secure_random_jwt_secret_key_here_should_be_at_least_256_bits
quizapp.jwtExpirationMs=3600000
```

### 4. Build the Application

Using Maven wrapper (recommended):

```bash
./mvnw clean install
```

Or using Maven:

```bash
mvn clean install
```

### 5. Run the Application

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📱 Usage

### First Time Setup

1. Navigate to `http://localhost:8080`
2. Click on "Create New Account"
3. Register as either a Teacher or Student
4. Login with your credentials

### For Teachers

1. **Create a Quiz:**
   - Go to Teacher Dashboard
   - Click "Create New Quiz"
   - Enter quiz title and description
   - Add questions with multiple-choice options

2. **Publish a Quiz:**
   - From the dashboard, click "Publish" on a quiz
   - Share the unique quiz code with students

3. **Monitor Progress:**
   - View student results in real-time
   - Access leaderboards for each quiz
   - Track overall performance metrics

### For Students

1. **Join a Quiz:**
   - Click "Join Quiz"
   - Enter the quiz code provided by your teacher
   - Start the quiz

2. **Take a Quiz:**
   - Read each question carefully
   - Select your answer
   - Submit when complete

3. **View Results:**
   - Check your score and correct answers
   - Compare your performance on the leaderboard

## 🧪 Testing

Run all tests:

```bash
./mvnw test
```

The test suite uses H2 in-memory database for fast execution.

## 📁 Project Structure

```
QuizApp/
├── src/
│   ├── main/
│   │   ├── java/com/example/quizzapp/
│   │   │   ├── config/          # Security & configuration
│   │   │   ├── controller/      # REST & MVC controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── model/           # Entity models
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── security/        # JWT & authentication
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf templates
│   │       └── application.properties
│   └── test/
│       ├── java/                # Test classes
│       └── resources/
│           └── application-test.properties
├── pom.xml
└── README.md
```

## 🔐 Default Test Accounts

For development and testing:

| Username | Password | Role |
|----------|----------|------|
| teacher | teacher123 | Teacher |
| student | student123 | Student |
| admin | admin123 | Admin |

**⚠️ Important:** Change these credentials in production!

## 🚧 Development

### Database Configuration

For development, you can use H2 in-memory database by updating `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

Enable H2 console:

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Hot Reload

For development with hot reload, use Spring Boot DevTools (already included in dependencies).

## 🐛 Troubleshooting

### Database Connection Issues

If you encounter database connection errors:

1. Verify PostgreSQL is running
2. Check database credentials
3. Ensure the database exists
4. Check PostgreSQL port (default: 5432)

### Port Already in Use

If port 8080 is already in use, change it in `application.properties`:

```properties
server.port=8081
```

### Memory Issues

If you encounter OutOfMemoryError, increase JVM heap size:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1024m"
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is open source and available under the MIT License.

## 👥 Authors

- **Jashdotcom** - *Initial work* - [GitHub](https://github.com/Jashdotcom)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap for the responsive UI components
- All contributors and testers

## 📧 Support

For support, email your-email@example.com or open an issue in the GitHub repository.

## 🔮 Future Enhancements

- [ ] Quiz categories and tags
- [ ] Question banks and templates
- [ ] Export results to CSV/PDF
- [ ] Email notifications
- [ ] Mobile application
- [ ] Real-time quiz mode
- [ ] Advanced analytics and reporting
- [ ] Integration with LMS platforms
- [ ] Multimedia question support (images, videos)
- [ ] Randomized question order
- [ ] Time-limited quizzes

---

**Happy Quizzing! 🎓**
