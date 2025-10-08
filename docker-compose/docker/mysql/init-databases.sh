#!/bin/bash
set -e



mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS growmeusers CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    CREATE DATABASE IF NOT EXISTS growmeproducts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    CREATE DATABASE IF NOT EXISTS growmeorders CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    CREATE DATABASE IF NOT EXISTS growmeemails CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    CREATE DATABASE IF NOT EXISTS growmepreorders CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOSQL


mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
    CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'password12345';
    GRANT ALL PRIVILEGES ON growmeusers.* TO 'root'@'%';
    GRANT ALL PRIVILEGES ON growmeproducts.* TO 'root'@'%';
    GRANT ALL PRIVILEGES ON growmeorders.* TO 'root'@'%';
    GRANT ALL PRIVILEGES ON growmeemails.* TO 'root'@'%';
    GRANT ALL PRIVILEGES ON growmepreorders.* TO 'root'@'%';
    FLUSH PRIVILEGES;
EOSQL