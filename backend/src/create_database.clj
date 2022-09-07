(ns create-database
  (:require [babashka.pods :as pods]
            [babashka.deps :as deps]
            [clojure.edn :as edn]))

(pods/load-pod 'org.babashka/go-sqlite3 "0.1.0")
(require '[pod.babashka.go-sqlite3 :as sqlite])

(deps/add-deps '{:deps {honeysql/honeysql {:mvn/version "1.0.444"}}})

(require
  '[honeysql.core :as sql]
  '[honeysql.helpers :as helpers])

;; adding a normal Clojure dependency
(deps/add-deps '{:deps {io.github.philippkueng/datalite
                        {:git/sha "9e44243e1b969f032a5154e85be3528cca9a23ae"}}})
(require '[datalite.schema :as ds])

(def schema [{:db/ident :job/gitlab-id
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one}
             {:db/ident :job/started-at
              ;; todo - maybe change to a proper date type?
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :job/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :job/output
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/full-text-search true}])

(defn map->nsmap
  "Utility function to namespace all the keys of a map
   from https://stackoverflow.com/questions/43722091/clojure-programmatically-namespace-map-keys"
  [m n]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                  (not (qualified-keyword? k)))
                              (keyword (str n) (name k))
                              k)]
                 (assoc acc new-kw v)))
    {} m))

(comment
  (ds/create-table-commands schema)

  (sqlite/execute! "test.db"
    ["create table if not exists foo (the_text TEXT, the_int INTEGER, the_real REAL, the_blob BLOB)"])

  ;; create the tables according to the schema
  (sqlite/execute! "test.db"
    (-> schema ds/create-table-commands vec))

  ;; list all the available tables
  (sqlite/query "test.db"
    ["SELECT name FROM sqlite_schema WHERE type = 'table' AND name NOT LIKE 'sqlite_%'"])
  ;=> [{:name "foo"} {:name "job"}]

  (def tweet-schema [{:db/ident :tweet/author_id
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one}
                     {:db/ident :tweet/text
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one}
                     {:db/ident :tweet/tweet_id
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one}
                     {:db/ident :tweet/created_at
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one}
                     {:db/ident :tweet/lang
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one}])

  (sqlite/execute! "test.db"
    (-> tweet-schema ds/create-table-commands vec))

  (->> (slurp "tweets.edn")
    (edn/read-string)
    (map (fn [tweet]
           (merge (select-keys tweet [:author_id :text :created_at :lang])
             {:tweet_id (:id tweet)})))
    (map #(map->nsmap % :tweet))
    first)
  ;=>
  ;{::tweet/author_id "10255262",
  ; ::tweet/text "A lot of managers, especially those from an engineering background, think that management is about doing stuff: defining rules, policies, and procedures; assigning tasks; creating external incentives; fixing problems.",
  ; ::tweet/created_at "2022-09-01T23:49:46.000Z",
  ; ::tweet/lang "en",
  ; ::tweet/tweet_id "1565487105907703808"}

  (def insertion (let [data (->> (slurp "tweets.edn")
                              (edn/read-string)
                              (map (fn [tweet]
                                     (merge (select-keys tweet [:author_id :text :created_at :lang])
                                       {:tweet_id (:id tweet)}))))
                       columns (into [] (keys (first data)))
                       rows (->> data
                              (take 2)
                              (map #(into [] (vals %)))
                              (into []))]
                   (-> (helpers/insert-into :tweet)
                     (helpers/values (into [] data))
                     (sql/format {:pretty true}))))

  (sqlite/execute! "test.db" insertion)

  )

