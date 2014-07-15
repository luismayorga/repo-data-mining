(ns nose.transform.core
  (:gen-class)
  (:require [nose.transform.extract :as extract]
            [nose.transform.load :as ld]
            [nose.transform.db :as db]))


(defn run [folder]
  (-> folder
      extract/folder|data
      ld/store-data))

(defn clean []
  (db/clean-db)
  (db/create-db))
