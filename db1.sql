CREATE DATABASE  IF NOT EXISTS `Manh` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `Manh`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: Manh
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `quantity` int DEFAULT '1',
  `added_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `cart_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `product_id` (`product_id`),
  KEY `FKpcttvuq4mxppo8sxggjtn5i2c` (`cart_id`),
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (28,NULL,6,1,'2025-07-07 03:20:03',1);
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK64t7ox312pqal3p7fg9o503c2` (`user_id`),
  CONSTRAINT `FKb5o626f86h46m4s7ms6ginnop` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,18);
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Đồng hồ nam'),(2,'Đồng hồ nữ'),(3,'Đồng hồ đôi'),(4,'Đồng hồ thông minh');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjyu2qbqt8gnvno9oe9j2s2ldk` (`order_id`),
  KEY `FK4q98utpd73imf4yhttm3w0eax` (`product_id`),
  CONSTRAINT `FK4q98utpd73imf4yhttm3w0eax` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKjyu2qbqt8gnvno9oe9j2s2ldk` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `price` decimal(38,2) NOT NULL,
  `quantity` int NOT NULL,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1200000.00,1,39,2),(2,4400000.00,1,40,1),(3,79000000.00,1,41,3),(4,1200000.00,2,41,2),(5,249000000.00,1,41,4),(7,79000000.00,1,43,3),(8,209000.00,1,43,6),(9,79000000.00,1,44,3),(10,300000.00,3,45,8),(11,120000.00,1,46,7),(12,209000.00,1,47,6),(13,249000000.00,1,48,4),(14,209000.00,1,49,6),(15,4800.00,1,50,11),(16,340000.00,1,51,10),(17,4800.00,2,52,11),(21,209000.00,1,56,6),(22,470000.00,1,56,9);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `shipping_address` varchar(255) NOT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` decimal(19,2) NOT NULL,
  `status` varchar(255) NOT NULL,
  `payment_method_id` int DEFAULT NULL,
  `notes` text,
  `phone_number` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `FKa03ljb6t6oa6mqtoifuwkb0kw` (`payment_method_id`),
  CONSTRAINT `FKa03ljb6t6oa6mqtoifuwkb0kw` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_methods` (`id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_methods` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (43,18,'Phú thọ Vĩnh Hưng','2025-07-04 10:33:44',79209000.00,'PENDING',1,'hee','1234567890'),(44,18,'sdfsdsf','2025-07-04 15:16:30',79000000.00,'PENDING',1,'','1234567890'),(45,18,'sdfsdsf','2025-07-04 15:45:29',900000.00,'PENDING',1,'','1234567890'),(46,18,'sdfsdsf','2025-07-07 04:41:12',120000.00,'PENDING',1,'','1234567890'),(47,18,'sdfsdsf','2025-07-07 04:45:19',209000.00,'PENDING',1,'','1234567890'),(48,18,'sdfsdsf','2025-07-07 05:32:41',249000000.00,'PENDING',1,'','1234567890'),(49,18,'sdfsdsf','2025-07-07 08:42:11',209000.00,'PENDING',1,'','1234567890'),(50,18,'sdfsdsf','2025-07-07 09:39:47',4800.00,'PENDING',1,'','1234567890'),(51,18,'sdfsdsf','2025-07-07 09:40:10',340000.00,'PENDING',1,'','1234567890'),(52,18,'Xã Vĩnh Hưng','2025-07-07 09:41:53',9600.00,'PENDING',1,'','1234567890'),(56,18,'Xã Thanh Điền, Huyện Châu Thành','2025-07-07 10:19:33',679000.00,'PENDING',1,'','1234567891');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_methods`
--

DROP TABLE IF EXISTS `payment_methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_methods` (
  `id` int NOT NULL AUTO_INCREMENT,
  `method_name` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkxvufx13wi6icen4i2wltdj02` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_methods`
--

LOCK TABLES `payment_methods` WRITE;
/*!40000 ALTER TABLE `payment_methods` DISABLE KEYS */;
INSERT INTO `payment_methods` VALUES (1,'Chuyển khoản ngân hàng',_binary '\0','');
/*!40000 ALTER TABLE `payment_methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` decimal(38,2) NOT NULL,
  `quantity` int DEFAULT '0',
  `image_url` varchar(255) DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Cặp đôi DW Petite','Cặp đồng hồ nam nữ DW dành cho đôi tình nhân',4599999.98,2,'https://curnonwatch.com/blog/wp-content/uploads/2021/03/anh-dong-ho-dep-76.jpg',1,'2025-07-04 08:17:01',_binary ''),(2,'Đồng hồ Casio Nam MTP-V002','Đồng hồ nam giá rẻ thương hiệu Casio',1200000.00,7,'https://naidecor.vn/wp-content/uploads/2022/10/anh-san-khuech-tan-chup-anh-san-pham-dong-ho.jpg',1,'2025-06-27 14:42:42',_binary '\0'),(3,'Đồng hồ nam dây da Thụy Sỹ ','Đồng hồ nam Thụy Sỹ dây da cao cấp, thiết kế hiện đại, lộ cơ tinh xảo, mặt kính sapphire chống trầy, phù hợp mọi phong cách.',79000000.00,7,'https://bizweb.dktcdn.net/100/472/501/collections/dong-ho-nam-iw-carnival-708g-chinh-hang-dd-5.jpg?v=1689331474973',1,'2025-06-27 15:16:47',_binary '\0'),(4,'Đồng hồ nam Rolex Datejust mặt xanh dương ','Rolex Datejust dây demi thép không gỉ, mặt xanh dương sang trọng, cọc số khối kim loại cao cấp, kính sapphire chống trầy, máy automatic chuẩn Thụy Sỹ.\r\n',249000000.00,3,'https://louiskimmi.com/wp-content/uploads/2021/07/Dong-ho-Rolex-DateJust-Blue-Navi.jpg',1,'2025-06-27 15:21:17',_binary ''),(5,'Vòng Tay Hoa Cúc Đính Đá Cao Cấp','Chất liệu hợp kim mạ vàng hồng, thiết kế hoa cúc phối đá zircon sang trọng.\r\nPhối hợp hoàn hảo với đồng hồ, tạo thành set trang sức thời trang tinh tế.\r\nDễ phối đồ, phù hợp đi tiệc, hẹn hò, đi làm hoặc làm quà tặng.',120000000.00,6,'https://winwatch.vn/wp-content/uploads/2024/11/KEMIL-KM2406-6.webp',2,'2025-06-27 18:45:21',_binary ''),(6,'COMUDIR - Đồng hồ cơ kiểu skeleton','COMUDIR TD6 là mẫu đồng hồ nam thời trang với thiết kế sang trọng và mạnh mẽ. Dây đeo thép không gỉ phối màu vàng bạc nổi bật, mặt đồng hồ đen kết hợp kim và số La Mã mạ vàng tạo điểm nhấn sang trọng.',209000.00,1,'https://down-vn.img.susercontent.com/file/vn-11134207-7qukw-ljzfxmt2q1vm04',1,'2025-07-04 10:05:33',_binary '\0'),(7,'Đồng hồ nữ I&W Carnival IW536L-DT – Quartz','Đồng hồ dây thép, đồng hồ iw carnival, đồng hồ nữ, đồng hồ nữ dây thép, đồng hồ nữ iw carnival, đồng hồ pin, đồng hồ pin nữ',120000.00,4,'https://zenwatch.vn/wp-content/uploads/2023/11/Dong-ho-nu-IW-Carnival-536L-chinh-hang-avt-1-470x470.jpg',2,'2025-07-04 15:37:24',_binary '\0'),(8,'Đồng Hồ Casio Nữ LTP-E175MG-9EVDF Dây Đeo Kim Loại Dạng Lưới Mạ Vàng','Đồng hồ dây thép, đồng hồ iw carnival, đồng hồ nữ, đồng hồ nữ dây thép, đồng hồ nữ iw carnival, đồng hồ pin, đồng hồ pin nữ',300000.00,6,'https://dongho-citizen.com/upload/product/dong-ho-casio/LTP-E175MG-9EV-000.jpg',2,'2025-07-04 15:38:51',_binary '\0'),(9,'Đồng Hồ Nữ Chính Hãng CASIO LTP-1302DD-4A1V','Đồng hồ dây thép, đồng hồ iw carnival, đồng hồ nữ, đồng hồ nữ dây thép, đồng hồ nữ iw carnival, đồng hồ pin, đồng hồ pin nữ',470000.00,10,'https://cdn.casio-vietnam.vn/wp-content/uploads/2024/07/LTP-1302DD-4A1V.jpg',2,'2025-07-04 15:40:38',_binary '\0'),(10,'Combo Đồng Hồ Nữ JA-1248LG + Nữ Trang ESME ES096','Julius thương hiệu đồng hồ thời trang Hàn Quốc, chất lượng Nhật Bản sang trọng đẳng cấp và sắc màu tinh tế. Giúp chủ nhân tỏa sáng trước mọi ánh nhìn.',340000.00,2,'https://thejulius.com.vn/wp-content/uploads/2023/02/t%E1%BA%A3i-xu%E1%BB%91ng-12.webp',2,'2025-07-04 15:41:52',_binary '\0'),(11,'Đồng hồ nam đính đá Hanboro by Huboler Automatic dây cao su 44mm','Đồng hồ Hanboro nam dây cao su màu đen đại diện cho sự bí ẩn, sang trọng\r\nĐồng hồ cơ nam Hanboro by Huboler automatic Nhật bền bỉ và chuẩn xác',4800.00,12,'https://dwatch.com.vn/wp-content/uploads/2022/06/Dong-ho-Hanboro-nam-dinh-da.jpg',1,'2025-07-04 15:44:18',_binary '\0'),(12,'Cặp đôi DW Petite','đồng hồ đẹp',130000.00,12,'https://curnonwatch.com/blog/wp-content/uploads/2021/03/anh-dong-ho-dep-76.jpg',1,'2025-07-07 09:43:26',_binary ''),(13,'Đồng hồ nam dây da Thụy Sỹ ','đẹp',2500000.00,24,'https://down-vn.img.susercontent.com/file/vn-11134207-7qukw-ljzfxmt2q1vm04',1,'2025-07-07 10:11:36',_binary ''),(14,'Đồng hồ nam dây da Thụy Sỹ ','đẹp',130000000.00,12,'https://curnonwatch.com/blog/wp-content/uploads/2021/03/anh-dong-ho-dep-76.jpg',1,'2025-07-07 10:17:14',_binary ''),(15,'Đồng hồ nam dây da Thụy Sỹ ','đẹp',134499999.99,12,'https://bizweb.dktcdn.net/100/472/501/collections/dong-ho-nam-iw-carnival-708g-chinh-hang-dd-5.jpg?v=1689331474973',1,'2025-07-07 10:20:45',_binary '');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fullname` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (18,'Manh','bui452893@gmail.com','$2a$10$kZ94WrBHCcKcGxR8f4xyGOHkm3r5L0SlD6qkHUeJhei1QB81F3p.C','1234567890','sdfsdsf','USER','2025-06-24 23:51:40',NULL,''),(22,'admin','manh8893884656@gmail.com','$2a$10$RbVc02OTyNBGTuvY9ihsSeHkN.5C7vrFNiif0t1bM6bj42T/JKMS6','1234567890','Xã Thanh Điền, Huyện Châu Thành','ADMIN','2025-06-27 01:15:25',NULL,''),(24,NULL,'hieu33898@gmail.com','$2a$10$4xfbc0Hiw/sySm5592th4uO70nCor1Hm0Wrr3mVV6IRt3731atW.a','1234567890','Xã Thanh Điền, Huyện Châu Thành','USER',NULL,NULL,'hiếu');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-07 10:57:34
