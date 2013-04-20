(ns cljhouston.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [dieter.core :as dieter]
            [net.cgrand.enlive-html :refer [deftemplate defsnippet content clone-for attr-starts]]))

(def dieter-config-options {
  :engine :v8
  :compress false
  :asset-roots ["resources"]
  :cache-root "resources/asset-cache"
  :cache-mode :production})

(def asset-url-attr {:link :href :script :src :img :src :a :href})
(def dieter-asset-selector (set (map #(vector (vector (first %) (attr-starts (second %) "/assets/"))) asset-url-attr)))
(defn dieter-replace-asset-url [config]
      (letfn [(get-asset-name [value] (.substring value 8))]
             (fn [node]
                 (let [attrname (asset-url-attr (node :tag))]
                      (assoc-in node [:attrs attrname] (dieter/link-to-asset (get-asset-name (get-in node [:attrs attrname])) config))))))

(deftemplate base-view "base.html" [body]
  [:#content]
  (content body)
  ; replace asset urls with cache-busting asset urls
  dieter-asset-selector
  (dieter-replace-asset-url dieter-config-options))

(defsnippet index-view "index.html" [:body :> :*] [members meeting]
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

(defroutes site-routes
  (GET "/" [] (render))
  (route/resources "/")
  (route/not-found (render)))

(def app (-> site-routes
             handler/site
             (dieter/asset-pipeline dieter-config-options)))

(defn -main [& options]
  (let [port (Integer. (or (first options) 5000))
        mode (keyword (or (second options) :dev))]
       (run-jetty app {:port port})))