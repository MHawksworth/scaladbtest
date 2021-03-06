package scaladbtest

/*
* Copyright 2010 Ken Egervari
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import com.mchange.v2.c3p0.ComboPooledDataSource

object TestContext {
	val hsqldbDataSource = new ComboPooledDataSource()
	hsqldbDataSource.setDriverClass("org.hsqldb.jdbc.JDBCDriver")
	hsqldbDataSource.setJdbcUrl("jdbc:hsqldb:mem:scaladbtest")
	hsqldbDataSource.setUser("sa")
	hsqldbDataSource.setPassword("")

	val mysqlDataSource = new ComboPooledDataSource()
	mysqlDataSource.setDriverClass("com.mysql.jdbc.Driver")
	mysqlDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/scaladbtest")
	mysqlDataSource.setUser("sa")
	mysqlDataSource.setPassword("")

	//val statement = dataSource.getConnection.createStatement
	//statement.execute("SET DATABASE REFERENTIAL INTEGRITY FALSE;")
	//statement.close()
}