(ns nose.core2
  (:require [nose.data.extract :as extract]
            [nose.data.load :as ld]
            [nose.db :as db]))


(defn run [folder]
  (-> folder
      extract/folder|data
      ld/store-data))
