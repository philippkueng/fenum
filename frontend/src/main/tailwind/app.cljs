(ns tailwind.app
  (:require [reagent.dom :as dom]
            [tailwind.views :as views]
            [tailwind.db :as db]
            [tailwind.events :as events]
            ["tauri-plugin-sql-api$default" :as Database]))

#_(defn start-sidecar-process
    []
    (-> js/window
      (aget "__TAURI__")
      (aget "shell")
      (aget "Command")
      ((fn [obj]
         (.sidecar ^js obj
           "binaries/bb"
           (clj->js
             ["/Users/philippkueng/Downloads/http-server.clj"]
             #_["./sidecar/http-server.clj"]
             #_["sidecar/http-server.clj"]
             #_["./../Resources/sidecar/http-server.clj"]
             #_["./Resources/sidecar/http-server.clj"]
             ))))
      ;; start the sidecar process
      ((fn [obj]
         (.execute ^js obj)))))

(defn load-database []
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

(defn app
  []
  (if (:auth? @db/state)
    [views/authenticated]
    [views/public]))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (dom/render [app]
    (.getElementById js/document "app"))
  #_(start-sidecar-process))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start)
  (load-database))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
