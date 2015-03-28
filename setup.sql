CREATE DATABASE `metaliquid` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `storedquery` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rawQuery` longtext,
  `exampleQuery` longtext,
  `exampleOutput` longtext,
  `description` mediumtext,
  `queryset` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

  
  CREATE TABLE `order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bookId` int(11) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `tradableAmount` decimal(20,10) DEFAULT NULL,
  `currencyPair` varchar(45) DEFAULT NULL,
  `pairId` varchar(45) DEFAULT NULL,
  `timestamp` varchar(45) DEFAULT NULL,
  `limitPrice` decimal(20,10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8430041 DEFAULT CHARSET=utf8;

CREATE TABLE `book` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` varchar(45) DEFAULT NULL,
  `exchange` varchar(45) DEFAULT NULL,
  `pair` varchar(45) DEFAULT NULL,
  `complete` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17402 DEFAULT CHARSET=utf8;


CREATE TABLE `metaliquid`.`parameter` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `paramname` VARCHAR(45) NULL,
  `param` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `paramname_UNIQUE` (`paramname` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `param_UNIQUE` (`param` ASC));

CREATE TABLE `metaliquid`.`parameter_storedqueryjoin` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `parameterId` INT NULL,
  `soredquerysetid` INT NULL,
  PRIMARY KEY (`id`));
  
INSERT INTO `metaliquid`.`storedquery` (`rawQuery`, `exampleQuery`, `description`) VALUES ('set @row_num = 0; set @row_price = 0; set @row_target = "--target--"; set @bookId = --bookid--; select * , adjustedCost/target as average from (select limitPrice, tradableAmount, row_index, sum_tradableAmount, accumulatedCost, @row_target as target, accumulatedCost - ((sum_tradableAmount - @row_target) * limitPrice) as adjustedCost from (select * from (select *,@row_num := @row_num + 1 as row_index, @sum_tradableAmount := @sum_tradableAmount + tradableAmount as sum_tradableAmount, @row_price := @row_price + tradableAmount * limitPrice as accumulatedCost from metaliquid.order where bookId = @bookId and type = "--ordertype--" limit --limit--) as t cross join (select @sum_tradableAmount := 0 as reset) const) as t where sum_tradableAmount > @row_target limit 1 ) as final', 'First');
INSERT INTO `metaliquid`.`parameter` (`paramname`, `param`) VALUES ('Target Qty', '-target-');
INSERT INTO `metaliquid`.`parameter` (`paramname`, `param`) VALUES ('Book id', '-bookid-');
INSERT INTO `metaliquid`.`parameter` (`paramname`, `param`) VALUES ('Order type', '-ordertype-');
INSERT INTO `metaliquid`.`parameter` (`paramname`, `param`) VALUES ('Internal Record limit', '-limit-');
INSERT INTO `metaliquid`.`parameter_storedqueryjoin` (`parameterId`, `storedqueryid`) VALUES ('1', '1');
INSERT INTO `metaliquid`.`parameter_storedqueryjoin` (`parameterId`, `storedqueryid`) VALUES ('2', '1');
INSERT INTO `metaliquid`.`parameter_storedqueryjoin` (`parameterId`, `storedqueryid`) VALUES ('3', '1');
INSERT INTO `metaliquid`.`parameter_storedqueryjoin` (`parameterId`, `storedqueryid`) VALUES ('4', '1');



