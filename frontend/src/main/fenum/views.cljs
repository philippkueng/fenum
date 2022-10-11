(ns fenum.views
  (:require [fenum.events :as events]
            [fenum.subscriptions :as subscriptions]
            [fenum.utilities :as utilities]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [cljs.pprint :refer [pprint]]
            ["@tremor/react" :refer [Datepicker]]))

(defn- table-icon [selected?]
  [:svg {:class (str
                  "mr-3 h-6 w-6 "
                  (if selected?
                    "text-gray-500"
                    "text-gray-400 group-hover:text-gray-500"))
         :xmlns "http://www.w3.org/2000/svg", :fill "none", :viewbox "0 0 24 24", :stroke-width "1.5", :stroke "currentColor"}
   [:path {:stroke-linecap "round", :stroke-linejoin "round", :d "M3.375 19.5h17.25m-17.25 0a1.125 1.125 0 01-1.125-1.125M3.375 19.5h7.5c.621 0 1.125-.504 1.125-1.125m-9.75 0V5.625m0 12.75v-1.5c0-.621.504-1.125 1.125-1.125m18.375 2.625V5.625m0 12.75c0 .621-.504 1.125-1.125 1.125m1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125m0 3.75h-7.5A1.125 1.125 0 0112 18.375m9.75-12.75c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125m19.5 0v1.5c0 .621-.504 1.125-1.125 1.125M2.25 5.625v1.5c0 .621.504 1.125 1.125 1.125m0 0h17.25m-17.25 0h7.5c.621 0 1.125.504 1.125 1.125M3.375 8.25c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125m17.25-3.75h-7.5c-.621 0-1.125.504-1.125 1.125m8.625-1.125c.621 0 1.125.504 1.125 1.125v1.5c0 .621-.504 1.125-1.125 1.125m-17.25 0h7.5m-7.5 0c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125M12 10.875v-1.5m0 1.5c0 .621-.504 1.125-1.125 1.125M12 10.875c0 .621.504 1.125 1.125 1.125m-2.25 0c.621 0 1.125.504 1.125 1.125M13.125 12h7.5m-7.5 0c-.621 0-1.125.504-1.125 1.125M20.625 12c.621 0 1.125.504 1.125 1.125v1.5c0 .621-.504 1.125-1.125 1.125m-17.25 0h7.5M12 14.625v-1.5m0 1.5c0 .621-.504 1.125-1.125 1.125M12 14.625c0 .621.504 1.125 1.125 1.125m-2.25 0c.621 0 1.125.504 1.125 1.125m0 1.5v-1.5m0 0c0-.621.504-1.125 1.125-1.125m0 0h7.5"}]])

(defn- field-icon [selected?]
  [:svg {:class "w-6 h-6" :xmlns "http://www.w3.org/2000/svg", :fill "none", :viewbox "0 0 24 24", :stroke-width "1.5", :stroke "currentColor"}
   [:path {:stroke-linecap "round", :stroke-linejoin "round", :d "M5.25 8.25h15m-16.5 7.5h15m-1.8-13.5l-3.9 19.5m-2.1-19.5l-3.9 19.5"}]])

(defn- table [rows]
  (let [headers (->> rows
                  first
                  keys
                  (map name))]
    [:table {:class "min-w-full divide-y divide-gray-200"}
     [:thead {:class "bg-gray-50"}
      [:tr
       (for [header headers]
         ^{:key (str "table-" header)}
         [:th {:scope "col"
               :class "px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"}
          header])]]
     [:tbody {:class "bg-white divide-y divide-gray-200"}
      (for [row rows]
        ^{:key (str "row-" (:id row))}
        [:tr
         (for [cell (->> (vals row)
                      (map-indexed (fn [index value] {:index index
                                                      :value value})))]
           ^{:key (str "cell-" (:id row) "-" (:index cell))}
           [:td {:class "px-6 py-4"}
            (:value cell)])])]]))

(defn- vega-lite [data]
  (reagent/create-class
    {:display-name "vega-lite"
     :reagent-render (fn [data] [:div {:id "viz"}])
     :component-did-mount (fn []
                            (let [js-data (clj->js data)]
                              (js/vegaEmbed "#viz" js-data)))
     :component-did-update (fn [this]
                             (let [[_ data] (reagent/argv this)
                                   js-data (clj->js data)]
                               (js/vegaEmbed "#viz" js-data)))}))

(defn datepicker []
  [(reagent/adapt-react-class Datepicker) {}])

(defn sidebar []
  [:div {:class "hidden lg:flex lg:flex-shrink-0"}
   [:div {:class "flex flex-col w-64 border-r border-gray-200 pt-5 pb-4 bg-gray-100"}
    [:div {:class "h-0 flex-1 flex flex-col overflow-y-auto"}
     [:div {:class "px-3 mt-6 relative inline-block text-left"}
      [:div
       [:button {:type "button"
                 :on-click #(re-frame/dispatch [::events/toggle-user-dropdown])
                 :class "group w-full bg-gray-100 rounded-md px-3.5 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-pink-500"
                 :id "options-menu"
                 :aria-expanded "false"
                 :aria-haspopup "true"}
        [:span {:class "flex w-full justify-between items-center"}
         [:span {:class "flex min-w-0 items-center justify-between space-x-3"}
          [:svg {:xmlns "http://www.w3.org/2000/svg", :fill "none", :viewbox "0 0 24 24", :stroke-width "1.5", :stroke "currentColor", :class "w-6 h-6"}
           [:path {:stroke-linecap "round", :stroke-linejoin "round", :d "M20.25 6.375c0 2.278-3.694 4.125-8.25 4.125S3.75 8.653 3.75 6.375m16.5 0c0-2.278-3.694-4.125-8.25-4.125S3.75 4.097 3.75 6.375m16.5 0v11.25c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125V6.375m16.5 0v3.75m-16.5-3.75v3.75m16.5 0v3.75C20.25 16.153 16.556 18 12 18s-8.25-1.847-8.25-4.125v-3.75m16.5 0c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125"}]]
          [:span {:class "flex-1 min-w-0"}
           (let [database (re-frame/subscribe [::subscriptions/selected-database])]
             [:span {:class "text-gray-900 text-sm font-medium truncate"}
              (if @database
                (:name @database)
                "Select database...")])]]
         [:svg {:class "flex-shrink-0 h-5 w-5 text-gray-400 group-hover:text-gray-500" :xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 20 20" :fill "currentColor" :aria-hidden "true"}
          [:path {:fill-rule "evenodd" :d "M10 3a1 1 0 01.707.293l3 3a1 1 0 01-1.414 1.414L10 5.414 7.707 7.707a1 1 0 01-1.414-1.414l3-3A1 1 0 0110 3zm-3.707 9.293a1 1 0 011.414 0L10 14.586l2.293-2.293a1 1 0 011.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" :clip-rule "evenodd"}]]]]]
      (let [display-dropdown? (re-frame/subscribe [::subscriptions/user-dropdown?])]
        (when @display-dropdown?
          [:div {:class "z-10 mx-3 origin-top absolute right-0 left-0 mt-1 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 divide-y divide-gray-200 focus:outline-none" :role "menu" :aria-orientation "vertical" :aria-labelledby "options-menu"}
           [:div {:class "py-1" :role "none"}
            (let [databases (re-frame/subscribe [::subscriptions/available-databases])]
              (for [database (map-indexed (fn [index value]
                                            (assoc value :index index)) @databases)]
                ^{:key (str "available-database-" (:index database))}
                [:a {:href "#"
                     :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900"
                     :role "menuitem"
                     :on-click #(re-frame/dispatch [::events/load-database database])}
                 (:name database)]))]
           [:div {:class "py-1" :role "none"}
            [:a {:href "#"
                 :on-click #(println "not implemented")
                 :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900" :role "menuitem"}
             "Manage databases"]]]))]
     [:nav {:class "px-3 mt-6"}
      [:div
       [:h3 {:class "px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider" :id "teams-headline"} "Tables"]
       [:div {:class "space-y-1 mt-1"}
        (let [tables (re-frame/subscribe [::subscriptions/tables])
              selected-table (re-frame/subscribe [::subscriptions/selected-table])]
          (for [table (->> @tables
                        (map-indexed (fn [index value]
                                       (assoc value :index index
                                                    :selected? (= (:name value) (:name @selected-table)))))
                        doall)]
            ^{:key (str "table-" (:index table))}
            [:a {:href "#"
                 :class (str
                          "group flex items-center px-2 py-2 text-sm font-medium rounded-md "
                          (if (:selected? table)
                            "bg-gray-200 text-gray-900"
                            "text-gray-700 hover:text-gray-900 hover:bg-gray-50"))
                 :on-click #(re-frame/dispatch [::events/load-table table])}
             [table-icon (:selected? table)]
             (:name table)]))]]

      ;; selected fields
      #_[:div {:class "mt-8"}
         [:h3 {:class "px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider" :id "teams-headline"} "Selected fields"]
         [:div {:class "mt-1 space-y-1" :role "group" :aria-labelledby "teams-headline"}
          [:a {:href "#" :class "group flex items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md hover:text-gray-900 hover:bg-gray-50"}
           [:span {:class "w-2.5 h-2.5 mr-4 bg-pink-500 rounded-full" :aria-hidden "true"}]
           [:span {:class "truncate"} "Engineering"]]
          [:a {:href "#" :class "group flex items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md hover:text-gray-900 hover:bg-gray-50"}
           [:span {:class "w-2.5 h-2.5 mr-4 bg-green-500 rounded-full" :aria-hidden "true"}]
           [:span {:class "truncate"} "Human Resources"]]]]

      ;; available fields
      [:div {:class "mt-8"}
       [:h3 {:class "px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider" :id "teams-headline"} "Available fields"]
       [:div {:class "mt-1 space-y-1" :role "group" :aria-labelledby "teams-headline"}
        (let [available-fields (re-frame/subscribe [::subscriptions/available-fields])]
          (for [field (keys @available-fields)]
            ^{:key (str "field-" (name field))}
            [:a {:href "#" :class "group flex items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md hover:text-gray-900 hover:bg-gray-50"}
             [:span {:class "w-2.5 h-2.5 mr-4 bg-pink-500 rounded-full" :aria-hidden "true"}]
             [:span {:class "truncate"} (name field)]]))]]

      #_[:div {:class "mt-8"}
         [:h3 {:class "px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider" :id "teams-headline"} "Teams"]
         [:div {:class "mt-1 space-y-1" :role "group" :aria-labelledby "teams-headline"}
          [:a {:href "#" :class "group flex items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md hover:text-gray-900 hover:bg-gray-50"}
           [:span {:class "w-2.5 h-2.5 mr-4 bg-pink-500 rounded-full" :aria-hidden "true"}]
           [:span {:class "truncate"} "Engineering"]]
          [:a {:href "#" :class "group flex items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md hover:text-gray-900 hover:bg-gray-50"}
           [:span {:class "w-2.5 h-2.5 mr-4 bg-green-500 rounded-full" :aria-hidden "true"}]
           [:span {:class "truncate"} "Human Resources"]]]]]]]])

(defn main-panel []
  ;; main container
  [:div {:class "flex flex-col w-0 flex-1 overflow-hidden"}
   [:main {:class "flex-1 relative z-0 overflow-y-auto focus:outline-none" :tabIndex "0"}
    ;; top row with search and date buttons
    [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 flex flex-row items-end"}
     [:div {:class "basis-3/4"}
      [:label {:for "search", :class "block text-sm font-medium text-gray-700"} "Search Term"]
      [:div {:class "relative mt-1 rounded-md shadow-sm"}
       [:div {:class "pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3"}
        [:span {:class "text-gray-500 sm:text-sm"} "🔎"]]
       [:input {:type "text", :name "search", :id "search", :class "block w-full rounded-md border-gray-300 pl-10 pr-12 focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm", :placeholder "specific error message"
                ;; just print the input to the console...
                :on-change #(println (utilities/value %))}]]]
     [:div {:class "basis-1/4 pl-2 flex justify-end space-x-2"}
      [:button {:class "h-10 px-6 w-1/2 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"} "From"]
      [:button {:class "h-10 px-6 w-1/2 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
                :on-click #(println "TODO - clicked the `To` button")} "To"]]]

    ;; here I'd like a visualization of the entries within the given time frame
    (let [rows (re-frame/subscribe [::subscriptions/rows])
          data {"$schema" "https://vega.github.io/schema/vega-lite/v5.json"
                :description "A simple bar chart with embedded data"
                :data {:values (->> @rows
                                 (map (fn [row] {:created_at (:created_at row)
                                                 :character_count (count (:text row))})))}
                :mark "bar"
                :encoding {:x {:field "created_at"
                               :type "ordinal"}
                           :y {:field "character_count"
                               :type "quantitative"}}}]
      [vega-lite data])

    ;; results
    [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6"}
     [:div {:class "shadow overflow-hidden border-b border-gray-200 sm:rounded-lg"}
      (let [rows (re-frame/subscribe [::subscriptions/rows])]
        [table @rows])]]

    [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6"}
     [:h2 {:class "px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider"}
      "Debugging buttons"]
     [:button {:class "h-10 px-6 w-1/2 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
               :on-click #(do
                            (println "clicked the button")
                            (js/console.log (-> js/window
                                              (aget "__TAURI__")
                                              (aget "path")
                                              ((fn [path] (.resourceDir ^js path)))
                                              ((fn [promise] (.then ^js promise (fn [directory]
                                                                                  (re-frame/dispatch [::events/set-tauri-resource-dir directory]))))))))}
      "Read resourceDir"]]

    [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6"}
     [:h2 {:class "px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider"}
      "Select a database"]
     [:input {:type "file"
              :on-change #(do (println (-> % .-target .-value))
                              (js/console.log (-> % .-target .-files)))
              :accept ".sqlite"}]]

    [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6"}
     [:h2 {:class "px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider"}
      "Tremor integration"]
     [datepicker]]

    [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6"}
     (let [database (re-frame/subscribe [::subscriptions/raw-database])]
       [:pre (with-out-str (pprint (dissoc @database
                                     :user-dropdown?
                                     :available-databases)))])]

    #_[:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6 space-x-2"}
       [:button
        {:class "h-10 px-6 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
         :on-click #(do (println "clicked the button")
                        (.then
                          (.select ^js (:sqlite-db @db/state) "select name from sqlite_schema")
                          (fn [result]
                            (do
                              (swap! db/state assoc :query-result (js->clj result :keywordize-keys true))
                              (js/console.log result)))))}
        "Query the Database"]
       [:button
        {:class "h-10 px-6 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
         :on-click #(swap! db/state dissoc :query-result)}
        "Clear the result"]]
    #_[:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6 space-x-2"}
       [:input
        {:class "h-10 px-6 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
         :type "file"
         :id "file-upload"
         :on-change #(js/console.log (-> % .-target .-files))}]]
    #_[:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6 space-x-2"}
       [:button
        {:class "h-10 px-6 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
         :on-click #(do (println "clicked the button")
                        (let [db-path "/Users/philippkueng/Documents/Programmieren/Clojure/fenum/backend/test.db"]
                          (.then (.load Database (str "sqlite:" db-path))
                            (fn [database]
                              (events/load-sqlite-database database)))))}
        "Load a different database"]]]])

(defn window
  []
  (let [view (re-frame/subscribe [::subscriptions/view])]
    (case @view
      :main [:div {:class "h-screen flex overflow-hidden bg-white"}
             [sidebar]
             [main-panel]])))
