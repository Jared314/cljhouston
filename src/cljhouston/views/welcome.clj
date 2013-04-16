(ns cljhouston.views.welcome
  (:require [net.cgrand.enlive-html :refer :all]))

(deftemplate base-view "public/base.html" [body]
  [:#content]
  (content body))

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