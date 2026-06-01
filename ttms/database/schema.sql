CREATE DATABASE IF NOT EXISTS ttms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE ttms;

CREATE TABLE studios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  row_count INT NOT NULL,
  col_count INT NOT NULL,
  introduction VARCHAR(2000),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

CREATE TABLE seats (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  studio_id BIGINT NOT NULL,
  row_no INT NOT NULL,
  col_no INT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uk_seat_position (studio_id, row_no, col_no),
  CONSTRAINT fk_seat_studio FOREIGN KEY (studio_id) REFERENCES studios(id)
);

CREATE TABLE plays (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(100) NOT NULL,
  language VARCHAR(100) NOT NULL,
  name VARCHAR(200) NOT NULL,
  introduction VARCHAR(2000),
  poster_url VARCHAR(500),
  trailer_url VARCHAR(500),
  duration_minutes INT NOT NULL,
  base_price DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

CREATE TABLE schedules (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  studio_id BIGINT NOT NULL,
  play_id BIGINT NOT NULL,
  show_time DATETIME NOT NULL,
  ticket_price DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  KEY idx_schedule_play (play_id),
  KEY idx_schedule_studio_time (studio_id, show_time),
  CONSTRAINT fk_schedule_studio FOREIGN KEY (studio_id) REFERENCES studios(id),
  CONSTRAINT fk_schedule_play FOREIGN KEY (play_id) REFERENCES plays(id)
);

CREATE TABLE tickets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  seat_id BIGINT NOT NULL,
  schedule_id BIGINT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
  lock_time DATETIME NULL,
  version BIGINT DEFAULT 0,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uk_ticket_schedule_seat (schedule_id, seat_id),
  KEY idx_ticket_schedule_status (schedule_id, status),
  CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES seats(id),
  CONSTRAINT fk_ticket_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);

CREATE TABLE customers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  name VARCHAR(100),
  phone VARCHAR(30),
  email VARCHAR(100),
  balance DECIMAL(10,2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uk_customer_username (username)
);

CREATE TABLE employees (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_no VARCHAR(20) NOT NULL,
  name VARCHAR(100) NOT NULL,
  position VARCHAR(50) NOT NULL,
  phone VARCHAR(30),
  email VARCHAR(100),
  password_hash VARCHAR(100) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uk_employee_no (employee_no)
);

CREATE TABLE sales (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT NULL,
  customer_id BIGINT NULL,
  sale_time DATETIME NOT NULL,
  paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
  change_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
  sale_type VARCHAR(30) NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  KEY idx_sale_time (sale_time),
  CONSTRAINT fk_sale_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
  CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE sale_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sale_id BIGINT NOT NULL,
  ticket_id BIGINT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT fk_item_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
  CONSTRAINT fk_item_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);

CREATE TABLE roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uk_role_name (name)
);

CREATE TABLE resources (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(40) NOT NULL,
  name VARCHAR(100) NOT NULL,
  url VARCHAR(200) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

CREATE TABLE role_resources (
  role_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, resource_id),
  CONSTRAINT fk_rr_role FOREIGN KEY (role_id) REFERENCES roles(id),
  CONSTRAINT fk_rr_resource FOREIGN KEY (resource_id) REFERENCES resources(id)
);

CREATE TABLE employee_roles (
  employee_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (employee_id, role_id),
  CONSTRAINT fk_er_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
  CONSTRAINT fk_er_role FOREIGN KEY (role_id) REFERENCES roles(id)
);
