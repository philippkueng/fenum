(ns fenum.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame]
            [fenum.views :as views]
            [fenum.events :as events]
            [fenum.utilities :as utilities]))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (utilities/get-element-by-id "app")]
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
