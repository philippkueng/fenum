(ns tailwind.views
  (:require [tailwind.events :as events]
            [tailwind.db :as db]
            [cljs.pprint :refer [pprint]]
            ["tauri-plugin-sql-api$default" :as Database]))

(defn public
  []
  [:div {:class "min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8"}
   [:div {:class "sm:mx-auto sm:w-full sm:max-w-md"}
    [:h2 {:class "mt-6 text-center text-3xl font-extrabold text-gray-900"} "Sign in to your account"]
    [:p {:class "mt-2 text-center text-sm text-gray-600 max-w"} "Or "
     [:a {:href "#" :class "font-medium text-pink-600 hover:text-pink-500"} "start your 14-day free trial"]]]
   [:div {:class "mt-8 sm:mx-auto sm:w-full sm:max-w-md"}
    [:div {:class "bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10"}
     [:form {:class "space-y-6" :action "#" :method "POST"}
      [:div
       [:label {:for "email" :class "block text-sm font-medium text-gray-700"} "Email address"]
       [:div {:class "mt-1"}
        [:input {:id "email" :name "email" :type "email" :auto-complete "email" :required true :class "appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-pink-500 focus:border-pink-500 sm:text-sm"}]]]
      [:div
       [:label {:for "password" :class "block text-sm font-medium text-gray-700"} "Password"]
       [:div {:class "mt-1"}
        [:input {:id "password" :name "password" :type "password" :auto-complete "current-password" :required true :class "appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-pink-500 focus:border-pink-500 sm:text-sm"}]]]
      [:div {:class "flex items-center justify-between"}
       [:div {:class "flex items-center"}
        [:input {:id "remember_me" :name "remember_me" :type "checkbox" :class "h-4 w-4 text-pink-600 focus:ring-pink-500 border-gray-300 rounded"}]
        [:label {:for "remember_me" :class "ml-2 block text-sm text-gray-900"} "Remember me"]]
       [:div {:class "text-sm"}
        [:a {:href "#" :class "font-medium text-pink-600 hover:text-pink-500"} "Forgot your password?"]]]
      [:div
       [:button
        {:type "submit"
         :on-click #(events/login)
         :class "w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-pink-600 hover:bg-pink-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-pink-500"}
        "Sign in"]]]]]])

(defn authenticated
  []
  [:div {:class "h-screen flex overflow-hidden bg-white"}
   [:div {:class "hidden lg:flex lg:flex-shrink-0"}
    [:div {:class "flex flex-col w-64 border-r border-gray-200 pt-5 pb-4 bg-gray-100"}
     ;; title
     [:div {:class "flex items-center flex-shrink-0 px-6"}
      [:div {:class "text-center text-3xl font-bold text-gray-900"} "Fenum"]]
     [:div {:class "h-0 flex-1 flex flex-col overflow-y-auto"}
      [:div {:class "px-3 mt-6 relative inline-block text-left"}
       [:div
        [:button {:type "button"
                  :on-click #(events/toggle-user-dropdown)
                  :class "group w-full bg-gray-100 rounded-md px-3.5 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-pink-500"
                  :id "options-menu"
                  :aria-expanded "false"
                  :aria-haspopup "true"}
         [:span {:class "flex w-full justify-between items-center"}
          [:span {:class "flex min-w-0 items-center justify-between space-x-3"}
           [:img {:class "w-10 h-10 bg-gray-300 rounded-full flex-shrink-0" :src "https://images.unsplash.com/photo-1502685104226-ee32379fefbe?ixlib=rb-1.2.1&ixqx=4cZVjZZC0A&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=3&w=256&h=256&q=80" :alt ""}]
           [:span {:class "flex-1 min-w-0"}
            [:span {:class "text-gray-900 text-sm font-medium truncate"} "Jessy Schwarz"]
            [:br]
            [:span {:class "text-gray-500 text-sm truncate"} "@jessyschwarz"]]]
          [:svg {:class "flex-shrink-0 h-5 w-5 text-gray-400 group-hover:text-gray-500" :xmlns "http://www.w3.org/2000/svg" :viewBox "0 0 20 20" :fill "currentColor" :aria-hidden "true"}
           [:path {:fill-rule "evenodd" :d "M10 3a1 1 0 01.707.293l3 3a1 1 0 01-1.414 1.414L10 5.414 7.707 7.707a1 1 0 01-1.414-1.414l3-3A1 1 0 0110 3zm-3.707 9.293a1 1 0 011.414 0L10 14.586l2.293-2.293a1 1 0 011.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" :clip-rule "evenodd"}]]]]]
       (when (:user-dropdown? @db/state)
         [:div {:class "z-10 mx-3 origin-top absolute right-0 left-0 mt-1 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 divide-y divide-gray-200 focus:outline-none" :role "menu" :aria-orientation "vertical" :aria-labelledby "options-menu"}
          [:div {:class "py-1" :role "none"}
           [:a {:href "#" :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900" :role "menuitem"} "View profile"]
           [:a {:href "#" :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900" :role "menuitem"} "Settings"]
           [:a {:href "#" :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900" :role "menuitem"} "Notifications"]]
          [:div {:class "py-1" :role "none"}
           [:a {:href "#"
                :on-click #(events/logout)
                :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-gray-900" :role "menuitem"}
            "Logout"]]])]
      [:nav {:class "px-3 mt-6"}
       [:div {:class "space-y-1"}
        [:a {:href "#" :class "bg-gray-200 text-gray-900 group flex items-center px-2 py-2 text-sm font-medium rounded-md"}
         [:svg {:class "text-gray-500 mr-3 h-6 w-6" :xmlns "http://www.w3.org/2000/svg" :fill "none" :viewBox "0 0 24 24" :stroke "currentColor" :aria-hidden "true"}
          [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width "2" :d "M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"}]] "Home"]
        [:a {:href "#" :class "text-gray-700 hover:text-gray-900 hover:bg-gray-50 group flex items-center px-2 py-2 text-sm font-medium rounded-md"}
         [:svg {:class "text-gray-400 group-hover:text-gray-500 mr-3 h-6 w-6" :xmlns "http://www.w3.org/2000/svg" :fill "none" :viewBox "0 0 24 24" :stroke "currentColor" :aria-hidden "true"}
          [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width "2" :d "M4 6h16M4 10h16M4 14h16M4 18h16"}]] "My tasks"]]
       [:div {:class "mt-8"}
        [:h3 {:class "px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider" :id "teams-headline"} "Teams"]
        [:div {:class "mt-1 space-y-1" :role "group" :aria-labelledby "teams-headline"}
         [:a {:href "#" :class "group flex items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md hover:text-gray-900 hover:bg-gray-50"}
          [:span {:class "w-2.5 h-2.5 mr-4 bg-pink-500 rounded-full" :aria-hidden "true"}]
          [:span {:class "truncate"} "Engineering"]]
         [:a {:href "#" :class "group flex items-center px-3 py-2 text-sm font-medium text-gray-700 rounded-md hover:text-gray-900 hover:bg-gray-50"}
          [:span {:class "w-2.5 h-2.5 mr-4 bg-green-500 rounded-full" :aria-hidden "true"}]
          [:span {:class "truncate"} "Human Resources"]]]]]]]]
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
                 :on-change #(println (-> % .-target .-value))}]]]
      [:div {:class "basis-1/4 pl-2 flex justify-end space-x-2"}
       [:button {:class "h-10 px-6 w-1/2 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"} "From"]
       [:button {:class "h-10 px-6 w-1/2 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
                 :on-click #(println "TODO - clicked the `To` button")} "To"]]]

     ;; results
     #_[:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6"}
        [:div {:class "overflow-x-auto relative shadow-md sm:rounded-md"}
         [:table {:class "w-full text-sm text-left text-gray-500"}
          [:thead {:class "text-xs text-gray-700 bg-gray-200"}
           [:tr
            [:th {:scope "col", :class "py-3 px-6"} "Product name"]
            [:th {:scope "col", :class "py-3 px-6"}
             [:div {:class "flex items-center"}
              [:a {:href "#"}
               "Color"
               #_[:svg {:xmlns "http://www.w3.org/2000/svg", :class "ml-1 w-3 h-3", :aria-hidden "true", :fill "currentColor", :viewbox "0 0 320 512"}
                  [:path {:d "M27.66 224h264.7c24.6 0 36.89-29.78 19.54-47.12l-132.3-136.8c-5.406-5.406-12.47-8.107-19.53-8.107c-7.055 0-14.09 2.701-19.45 8.107L8.119 176.9C-9.229 194.2 3.055 224 27.66 224zM292.3 288H27.66c-24.6 0-36.89 29.77-19.54 47.12l132.5 136.8C145.9 477.3 152.1 480 160 480c7.053 0 14.12-2.703 19.53-8.109l132.3-136.8C329.2 317.8 316.9 288 292.3 288z"}]]]]]
            [:th {:scope "col", :class "py-3 px-6"}
             [:div {:class "flex items-center"}
              [:a {:href "#"}
               "Category"
               #_[:svg {:xmlns "http://www.w3.org/2000/svg", :class "ml-1 w-3 h-3", :aria-hidden "true", :fill "currentColor", :viewbox "0 0 320 512"}
                  [:path {:d "M27.66 224h264.7c24.6 0 36.89-29.78 19.54-47.12l-132.3-136.8c-5.406-5.406-12.47-8.107-19.53-8.107c-7.055 0-14.09 2.701-19.45 8.107L8.119 176.9C-9.229 194.2 3.055 224 27.66 224zM292.3 288H27.66c-24.6 0-36.89 29.77-19.54 47.12l132.5 136.8C145.9 477.3 152.1 480 160 480c7.053 0 14.12-2.703 19.53-8.109l132.3-136.8C329.2 317.8 316.9 288 292.3 288z"}]]]]]
            [:th {:scope "col", :class "py-3 px-6"}
             [:div {:class "flex items-center"}
              [:a {:href "#"}
               "Price"
               #_[:svg {:xmlns "http://www.w3.org/2000/svg", :class "ml-1 w-3 h-3", :aria-hidden "true", :fill "currentColor", :viewbox "0 0 320 512"}
                  [:path {:d "M27.66 224h264.7c24.6 0 36.89-29.78 19.54-47.12l-132.3-136.8c-5.406-5.406-12.47-8.107-19.53-8.107c-7.055 0-14.09 2.701-19.45 8.107L8.119 176.9C-9.229 194.2 3.055 224 27.66 224zM292.3 288H27.66c-24.6 0-36.89 29.77-19.54 47.12l132.5 136.8C145.9 477.3 152.1 480 160 480c7.053 0 14.12-2.703 19.53-8.109l132.3-136.8C329.2 317.8 316.9 288 292.3 288z"}]]]]]
            [:th {:scope "col", :class "py-3 px-6"}
             [:span {:class "sr-only"} "Edit"]]]]
          [:tbody
           [:tr {:class "bg-white border-b"}
            [:th {:scope "row", :class "py-4 px-6 font-medium text-gray-900 whitespace-nowrap"} "Apple MacBook Pro 17"]
            [:td {:class "py-4 px-6"}]
            [:td {:class "py-4 px-6"}]
            [:td {:class "py-4 px-6"} "$2999"]
            [:td {:class "py-4 px-6 text-right"}
             [:a {:href "#", :class "font-medium text-blue-600 hover:underline"} "Edit"]]]
           [:tr {:class "bg-white border-b"}
            [:th {:scope "row", :class "py-4 px-6 font-medium text-gray-900 whitespace-nowrap"} "Microsoft Surface Pro"]
            [:td {:class "py-4 px-6"}]
            [:td {:class "py-4 px-6"} "Laptop PC"]
            [:td {:class "py-4 px-6"} "$1999"]
            [:td {:class "py-4 px-6 text-right"}
             [:a {:href "#", :class "font-medium text-blue-600 hover:underline"} "Edit"]]]
           [:tr {:class "bg-white"}
            [:th {:scope "row", :class "py-4 px-6 font-medium text-gray-900 whitespace-nowrap"} "Magic Mouse 2"]
            [:td {:class "py-4 px-6"}]
            [:td {:class "py-4 px-6"}]
            [:td {:class "py-4 px-6"} "$99"]
            [:td {:class "py-4 px-6 text-right"}
             [:a {:href "#", :class "font-medium text-blue-600 hover:underline"} "Edit"]]]]]]]

     [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6"}
      [:pre (with-out-str (pprint (dissoc @db/state :auth?)))]]

     [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6 space-x-2"}
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
     [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6 space-x-2"}
      [:input
       {:class "h-10 px-6 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
        :type "file"
        :id "file-upload"
        :on-change #(js/console.log (-> % .-target .-files))}]]
     [:div {:class "px-4 mt-6 sm:px-6 lg:px-8 mt-6 space-x-2"}
      [:button
       {:class "h-10 px-6 border border-transparent text-sm font-medium rounded-md bg-gray-700 text-white shadow-sm"
        :on-click #(do (println "clicked the button")
                       (let [db-path "/Users/philippkueng/Documents/Programmieren/Clojure/fenum/backend/test.db"]
                         (.then (.load Database (str "sqlite:" db-path))
                           (fn [database]
                             (events/load-sqlite-database database)))))}
       "Load a different database"]]]]])