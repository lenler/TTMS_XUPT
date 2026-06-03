DROP TABLE IF EXISTS employee_roles;
DROP TABLE IF EXISTS role_resources;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS plays;
DROP TABLE IF EXISTS studios;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS customer_recharges;
DROP TABLE IF EXISTS customers;

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
  CONSTRAINT uk_seat_position UNIQUE (studio_id, row_no, col_no),
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
  CONSTRAINT uk_ticket_schedule_seat UNIQUE (schedule_id, seat_id),
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
  gender INT NOT NULL DEFAULT 0,
  payment_password VARCHAR(100),
  balance DECIMAL(10,2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT uk_customer_username UNIQUE (username)
);

CREATE TABLE customer_recharges (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  balance_after DECIMAL(10,2) NOT NULL,
  recharge_time DATETIME NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT fk_recharge_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
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
  CONSTRAINT uk_employee_no UNIQUE (employee_no)
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
  CONSTRAINT uk_role_name UNIQUE (name)
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
