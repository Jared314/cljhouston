(ns cljhouston.server
  (:require [cljhouston.views.welcome :as welcome]
            [ring.adapter.jetty :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [dieter.core :as dieter]))

(defroutes site-routes
  (GET "/" [] (welcome/render))
  (route/resources "/")
  (route/not-found (welcome/render)))

(def dieter-config-options {
  :engine :v8
  :compress false
  :asset-roots ["resources"]
  :cache-root "resources/asset-cache"
  :cache-mode :production})

(def app (-> site-routes
             handler/site
             (dieter/asset-pipeline dieter-config-options)))

(defn -main [& options]
  (let [port (Integer. (or (first options) 5000))
        mode (keyword (or (second options) :dev))]
       (run-jetty app {:port port})))