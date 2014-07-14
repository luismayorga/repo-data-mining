(ns nose.data.extract
  (:require [clojure.xml :as xml]
            [clojure.java.io :as io])
  (:import (java.io File)))


(defn- quality-xml? [f]
  (if 
    (.isDirectory f) 
    false
    (with-open [r (io/reader f)]
      (= (apply str (take 2 (line-seq r)))
         "<?xml version=\"1.0\"?><qualityModel>"))))


(defn- xmls [f]
  (filter quality-xml? (file-seq (new File f))))


(defn- xml|metadata [x]
  (let [nom (clojure.string/split (.getName x) #"_")]
    {:system (nth nom 0), :revision (nth nom 2)}))


(defn xml|map [x]
  (merge (xml/parse x) 
         (xml|metadata x)))


(defn folder|data [f]
  (map xml|map (xmls f)))
