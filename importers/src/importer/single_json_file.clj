(ns importer.single-json-file
  (:require [importer.utilities :refer [flatten-paths]]
            [babashka.pods :as pods]
            [babashka.deps :as deps]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.set :as set]))

;; Execute the script using a command like
;; bb -x importer.single-json-file/convert --input /home/user/uplink_messages.json --output /home/user/data.sqlite --table-name messages

;; SQLite related utilities

(pods/load-pod 'org.babashka/go-sqlite3 "0.1.0")
(require '[pod.babashka.go-sqlite3 :as sqlite])

(deps/add-deps '{:deps {honeysql/honeysql {:mvn/version "1.0.444"}}})
(require
  '[honeysql.core :as sql]
  '[honeysql.helpers :as helpers])

(deps/add-deps '{:deps {io.github.philippkueng/datalite
                        {:git/sha "1b6f61a6f6f7d1ccb5a33a7a83410e87f404b357"}}})
(require '[datalite.schema :as ds])

;; Conversion code

(defn convert
  [{:keys [input output table-name]}]
  (let [data (->> (-> (io/reader input)
                    (json/parsed-seq true))
               (map #(->> (flatten-paths % "__")
                       (map (fn [[key val]]
                              (when-not (vector? val) {key val})))
                       (remove nil?)
                       (apply merge))))
        column-types (reduce (fn [column-types record]
                               (let [record-types (->> (keys record)
                                                    (map (fn [key]
                                                           {key #{(cond
                                                                    (string? (get record key)) :db.type/string
                                                                    (number? (get record key)) :db.type/long
                                                                    (boolean? (get record key)) :db.type/boolean
                                                                    :else (prn (get record key)))}}))
                                                    (apply merge))]
                                 (merge-with set/union column-types record-types))) {} data)
        ;; todo it could be that we got multiple types per column in which case some
        ;;  automatic coercion might be needed.
        schema (->> column-types
                 (map (fn [[key types]]
                        {:db/ident (keyword table-name (name key))
                         :db/valueType (first types)
                         :db/cardinality :db.cardinality/one})))]

    ;; create the table according to the schema
    (sqlite/execute! output
      (-> schema ds/create-table-commands vec))

    ;; insert the data
    (doseq [batch (partition 10 10 nil data)]
      (sqlite/execute! output
        (-> (helpers/insert-into (keyword table-name))
          (helpers/values batch)
          (sql/format {:pretty true}))))))
