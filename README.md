# 🎯 JobHunter - Full Stack Job Board Application

A modern, full-stack job board application built with Spring Boot (backend) and React + TypeScript (frontend), featuring comprehensive job management, user authentication, company profiles, and resume handling.

## ✨ Features

### 🔐 Authentication & Authorization
- JWT-based authentication with access and refresh tokens
- Role-based access control (Admin, HR, User)
- Permission management system
- Secure password handling

### 👔 Job Management
- Create, update, and delete job postings
- Advanced job search and filtering
- Job categories and skill requirements
- Application tracking and management

### 🏢 Company Management
- Company profiles with logo upload
- Company job listings
- Company details and information

### 📄 Resume & Application System
- Resume upload and management
- Job application tracking
- Application status updates
- CV/Resume file handling

### 👥 User Management
- User registration and profile management
- Admin dashboard for user management
- User activity tracking

### 📊 Admin Dashboard
- Comprehensive admin panel
- Statistics and analytics
- System management tools
- Data visualization

## 🛠 Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Database**: MySQL 5.7
- **Security**: Spring Security + JWT
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Template Engine**: Thymeleaf

### Frontend
- **Framework**: React 18.2.0
- **Language**: TypeScript
- **UI Library**: Ant Design (antd) 5.13.1
- **State Management**: Redux Toolkit
- **Build Tool**: Vite
- **HTTP Client**: Axios
- **Styling**: SCSS Modules

### DevOps & Deployment
- **Containerization**: Docker & Docker Compose
- **Web Server**: Nginx
- **Database**: MySQL
- **File Storage**: Local file system

## 🏗 Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │
│   React SPA     │◄───│     Nginx       │───►│  Spring Boot    │
│   (Frontend)    │    │  (Reverse Proxy)│    │   (Backend)     │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                                       │
                                              ┌─────────────────┐
                                              │                 │
                                              │    MySQL DB     │
                                              │                 │
                                              └─────────────────┘
```

## 📋 Prerequisites

- **Java 21** or higher
- **Node.js 18** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose**
- **MySQL 5.7+** (for local development)

## 🚀 Installation & Setup

### Development Setup

#### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/nvminh162/jobhunter-app.git
   cd jobhunter-app
   ```

2. **Setup MySQL Database**
   ```bash
   # Create database
   mysql -u root -p
   CREATE DATABASE nvminh162_jobhunter;
   ```

3. **Configure Backend**
   ```bash
   cd jobhunter-backend
   # Update application.properties with your database credentials
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

4. **Run Backend**
   ```bash
   # Using Maven
   ./mvnw spring-boot:run
   
   # Or using IDE
   # Import project and run JobhunterBackendApplication.java
   ```

   Backend will be available at: `http://localhost:8080`

#### Frontend Setup

1. **Install Dependencies**
   ```bash
   cd jobhunter-frontend
   npm install
   ```

2. **Configure Environment**
   ```bash
   # Copy environment file
   cp .env.development.example .env.development
   ```

3. **Run Frontend**
   ```bash
   npm run dev
   ```

   Frontend will be available at: `http://localhost:5173`

### Docker Deployment

#### 🐳 Quick Deploy with Docker Compose

1. **Build and Deploy**
   ```bash
   # Build frontend assets
   cd jobhunter-frontend
   npm install
   npm run build
   
   # Deploy with Docker Compose
   cd ../build-docker
   docker-compose up -d
   ```

2. **Access Application**
   - **Frontend**: `http://localhost` (Port 80)
   - **Backend API**: `http://localhost:8080`
   - **Database**: `localhost:3307`

#### 🔧 Docker Services

| Service | Port | Description |
|---------|------|-------------|
| nginx | 80, 443 | Web server serving React app |
| backend-spring | 8080 | Spring Boot REST API |
| db-mysql | 3307 | MySQL database |

#### 📂 Docker Volumes

- `../jobhunter-frontend/dist` → `/usr/share/nginx/html`
- `./nginx/default.conf` → `/etc/nginx/conf.d/default.conf`
- `../jobhunter-storage` → Application file storage

## 📚 API Documentation

### Authentication Endpoints
```
POST /api/v1/auth/login          # User login
POST /api/v1/auth/register       # User registration
POST /api/v1/auth/refresh        # Refresh access token
POST /api/v1/auth/logout         # User logout
```

### Job Management
```
GET    /api/v1/jobs              # Get all jobs
POST   /api/v1/jobs              # Create new job
GET    /api/v1/jobs/{id}         # Get job by ID
PUT    /api/v1/jobs/{id}         # Update job
DELETE /api/v1/jobs/{id}         # Delete job
```

### Company Management
```
GET    /api/v1/companies         # Get all companies
POST   /api/v1/companies         # Create company
GET    /api/v1/companies/{id}    # Get company details
PUT    /api/v1/companies/{id}    # Update company
DELETE /api/v1/companies/{id}    # Delete company
```

### Resume & Applications
```
GET    /api/v1/resumes           # Get user resumes
POST   /api/v1/resumes           # Submit job application
GET    /api/v1/resumes/{id}      # Get resume details
PUT    /api/v1/resumes/{id}      # Update application status
DELETE /api/v1/resumes/{id}      # Delete resume
```

### Admin Endpoints
```
GET    /api/v1/users             # Get all users (Admin)
POST   /api/v1/users             # Create user (Admin)
PUT    /api/v1/users/{id}        # Update user (Admin)
DELETE /api/v1/users/{id}        # Delete user (Admin)
```

**Swagger Documentation**: `http://localhost:8080/swagger-ui.html`

## ⚙ Environment Configuration

### Backend Configuration

**application.properties**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/nvminh162_jobhunter
spring.datasource.username=root
spring.datasource.password=root

# JWT Configuration
nvminh162.jwt.base64-secret=your-secret-key
nvminh162.jwt.access-token-validity-in-seconds=8640000
nvminh162.jwt.refresh-token-validity-in-seconds=8640000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Frontend Configuration

**.env.development**
```env
VITE_BACKEND_URL=http://localhost:8080
VITE_API_VERSION=v1
VITE_APP_TITLE=JobHunter
```

**.env.production**
```env
VITE_BACKEND_URL=https://your-production-api.com
VITE_API_VERSION=v1
VITE_APP_TITLE=JobHunter
```

## 🗄 Database Schema

### Core Entities

- **User**: User accounts with roles and permissions
- **Company**: Company profiles and information
- **Job**: Job postings with requirements and details
- **Resume**: User resumes and job applications
- **Skill**: Skills and technologies
- **Role**: User roles and permissions
- **Permission**: Granular access control

### Sample Data

Initial data is provided in:
- `jobhunter-backend/data/dumpdata.sql` - Sample data
- `jobhunter-backend/data/drop.sql` - Database cleanup

## 📁 Project Structure

```
jobhunter-app/
├── jobhunter-backend/          # Spring Boot Backend
│   ├── src/main/java/com/nvminh162/jobhunter/
│   │   ├── config/            # Security, CORS, JWT config
│   │   ├── controller/        # REST API controllers
│   │   ├── domain/           # JPA entities
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── repository/       # Data access layer
│   │   ├── service/          # Business logic
│   │   └── util/             # Utility classes
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── templates/        # Email templates
│   └── postman/             # API collection
├── jobhunter-frontend/         # React Frontend
│   ├── src/
│   │   ├── components/       # Reusable components
│   │   ├── pages/           # Page components
│   │   ├── redux/           # State management
│   │   ├── config/          # API configuration
│   │   ├── types/           # TypeScript types
│   │   └── styles/          # SCSS stylesheets
│   └── public/              # Static assets
├── jobhunter-storage/          # File uploads
├── build-docker/              # Docker deployment
│   ├── docker-compose.yml
│   └── nginx/
└── upload-docker/             # Docker uploads
```

## 🚦 Development Workflow

1. **Start Backend**: `cd jobhunter-backend && ./mvnw spring-boot:run`
2. **Start Frontend**: `cd jobhunter-frontend && npm run dev`
3. **Access Application**: `http://localhost:5173`
4. **API Testing**: Use Postman collection in `jobhunter-backend/postman/`

## 🧪 Testing

### Backend Testing
```bash
cd jobhunter-backend
./mvnw test
```

### Frontend Testing
```bash
cd jobhunter-frontend
npm run test
```

## 📦 Production Build

### Frontend Build
```bash
cd jobhunter-frontend
npm run build
```

### Backend Build
```bash
cd jobhunter-backend
./mvnw clean package -DskipTests
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**nvminh162**
- GitHub: [@nvminh162](https://github.com/nvminh162)
- Facebook: [@nvminh162](https://facebook.com/nvminh162)

## 🙏 Acknowledgments

- Spring Boot community
- React community
- Ant Design team
- All contributors and supporters

---

**Happy Job Hunting! 🎯**