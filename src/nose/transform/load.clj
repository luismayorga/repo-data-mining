(ns nose.transform.load
  (:require [nose.transform.db :as db]
            [clojure.zip :as z]
            [clojure.data.zip :as dz]
            [clojure.data.zip.xml :as dzx]
            [clojure.java.jdbc :as jdbc]))


(defn- store-report [con report]
  (doseq [entity (dzx/xml-> (z/xml-zip (:xml report)) :entity)]
    (let [entity-keys (jdbc/insert! con 
                                   :entity 
                                   {:system (:system report)
                                    :rev (:revision report)
                                    :type (dzx/attr entity :type)
                                    :name (dzx/attr entity :name)
                                    :factor (dzx/attr entity :factor)
                                    :package (dzx/attr entity :package)})]
      (doseq [flaw (dzx/xml-> entity :designFlaw)]
        (jdbc/insert! con
                      :flaw
                      {:entity_id  ((keyword "last_insert_rowid()") (first entity-keys))
                      :type (dzx/attr flaw :type) 
                      :severity (dzx/attr flaw :severity)
                      :impact (dzx/attr flaw :impact)
                      :categories (dzx/attr flaw :categories)})))))

(defn store-data [reports]
  (jdbc/with-db-transaction [con (db/db-connection)]
    (doseq [report reports]
      (store-report con report))))
