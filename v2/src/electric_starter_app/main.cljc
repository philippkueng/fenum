(ns electric-starter-app.main
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]
            #?(:clj [clojure.java.jdbc :as jdbc])))

#?(:clj
   (def db-uri "jdbc:sqlite:home-assistant_v2.db"))

#?(:clj (defonce db
          {:connection (jdbc/get-connection {:connection-uri db-uri})}))

#?(:clj (defn get-tables [query]
          (jdbc/query
            {:connection (jdbc/get-connection {:connection-uri db-uri})}
            query)
          ))


(e/defn Main [ring-request]
  (e/client
    (binding [dom/node js/document.body]
      (dom/h1 (dom/text "SQL UI"))
      (dom/span
        (dom/text "Use a query such as: ")
        (dom/code (dom/text "SELECT    * FROM     sqlite_schema WHERE   type ='table' AND   name NOT LIKE 'sqlite_%'")))
      (let [!query (atom "something")
            query (e/watch !query)]
        (dom/div
          (let [!local-query (atom "")
                local-query (e/watch !local-query)]
            (ui/textarea local-query
              (e/fn [v] (reset! !local-query v))
              (dom/props {:placeholder "select * from events"
                          :cols 100
                          :rows 10}))
            (ui/button
              (e/fn []
                (reset! !query local-query))
              (dom/text "Execute Query!"))))
        (e/server
          (let [result (get-tables query)
                columns (-> result first keys sort)]
            (e/client
              (dom/table
                (dom/tr
                  (e/for [column columns]
                    (dom/th (dom/text column))))
                (e/for [row result]
                  (dom/tr
                    (e/for [column columns]
                      (dom/td (dom/text (get row column))))))))))))))
