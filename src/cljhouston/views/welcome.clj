(ns cljhouston.views.welcome
  (:require [net.cgrand.enlive-html :refer :all]
            [dieter.core :as dieter]))

(def asset-url-attr {:link :href :script :src :img :src :a :href})
(def asset-selector (set (map #(vector (vector (first %) (attr-starts (second %) "/assets/"))) asset-url-attr)))
(defn get-asset-name [value] (.substring value 8))
(defn replace-asset-url [node]
      (let [attrname (-> node :tag asset-url-attr)]
           (assoc-in node [:attrs attrname] (dieter/link-to-asset (get-asset-name (-> node :attrs attrname))))))

(deftemplate base-view "public/base.html" [body]
  [:#content]
  (content body)
  ; replace asset urls with cache-busting asset urls
  [[:link (attr= :href "/assets/site.css")]]
  (set-attr :href (dieter/link-to-asset "site.css"))
  [[:script (attr= :src "/assets/app.js")]]
  (set-attr :src (dieter/link-to-asset "app.js")))

(defsnippet index-view "public/index.html" [:body :> :*] [members meeting]
  [:ul#members :li]
  (clone-for [m members] (content m))

  [:#meeting-date]
  (content (first meeting))
  
  [:#meeting-description]
  (content (second meeting)))

(def members
  ["Daniel Solano Gómez"
   "Nelson Morris"
   "Steven Byrnes"
   "Steven Reynolds"
   "Jim Theriot"
   "Robert Boone"
   "Jeremey Barrett"])

(def meetings
  [["5/24/2012" "Coding: Work on the website."]
   ["7/26/2012" "Clojure Macros through the Lens of Generative programming"]
   ["8/23/2012" "Watch video from clojure/west"]
   ["9/27/2012" "Pulling data from a database + Coding"]
   ["10/25/2012" "How to use Enlive"]])

(defn render [] (base-view (index-view members (last meetings))))
