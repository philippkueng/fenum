(ns fenum.db)

(def default-db
  {:view :main
   :user-dropdown? true
   :available-databases [{:name "local database"
                          :path "local.db"}
                         {:name "tweets database"
                          :path "/Users/philippkueng/Documents/Programmieren/Clojure/fenum/backend/test.db"}
                         {:name "bitfondue chunks"
                          :path "/Users/philippkueng/Documents/bitfondue/bitfondue/sqlite-explorations/sample.db"}]})