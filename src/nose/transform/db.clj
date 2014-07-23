(ns nose.transform.db
  (:require [clojure.java.jdbc :as jdbc])
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))


(def db-spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "./smell_history.sqlite"})

(defn- pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec)) 
               (.setJdbcUrl (str "jdbc:" 
                                 (:subprotocol spec) 
                                 ":" 
                                 (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               (.setMaxIdleTimeExcessConnections (* 30 60))
               (.setMaxIdleTime (* 3 60 60)))] 
    {:datasource cpds}))

(def pooled-db (delay (pool db-spec)))

(defn db-connection [] @pooled-db)


(defn create-db []
  (jdbc/db-do-commands (db-connection)
                       (jdbc/create-table-ddl :entity
                                              [:system :text]
                                              [:rev :integer]
                                              [:type :text]
                                              [:name :text]
                                              [:factor :text]
                                              [:package :text])

                       (jdbc/create-table-ddl :flaw
                                              [:entity_id :integer]
                                              [:type :text]
                                              [:severity :integer]
                                              [:impact :float]
                                              [:categories :text])))


(defn clean-db []
  (jdbc/db-do-commands (db-connection)
                       (jdbc/drop-table-ddl :entity)
                       (jdbc/drop-table-ddl :flaw)))

