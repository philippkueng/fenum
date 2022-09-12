(ns fenum.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame]
            [fenum.views :as views]
            [fenum.events :as events]
            ["tauri-plugin-sql-api$default" :as Database]))

#_(defn load-database []
    (.then (.load Database "sqlite:fenum.db")
      (fn [database]
        (events/load-sqlite-database database))))

(comment
  (def db (atom nil))

  ;; create the database
  (.then (.load Database "sqlite:fenum.db")
    (fn [database]
      (reset! db database)))

  ;; await db.execute('INSERT INTO ...')
  (.then
    (.execute @db "create table something (id integer, text text)")
    #(println "hopefully created the table"))

  ;; insert a table

  ;; query for the available tables?
  (.then
    (.select @db "select name from sqlite_schema")
    #(js/console.log %))


  (println "testing")
  )

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
