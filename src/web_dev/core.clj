(ns web-dev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [clojure.pprint :as pprint]
            [compojure.core :as c]
            [compojure.route :as route]))

; ring SPEC : https://github.com/ring-clojure/ring/blob/master/SPEC

(defonce server (atom nil))                                 ;; for saving run-jetty return object


;; synchronous handler
(defn app [req]
  (case (:uri req)
    "/" {:status 200
         :body "<h1>Homepage</h1>
                  <ul>
                    <li><a href=\"/echo\">Echo request</a></li>
                    <li><a href=\"/greeting\">Greeting</a></li>
                  </ul"
         :headers {"Content-Type" "text/html; charset=UTF-8"}}
    "/echo" {:status  200
             :body    (with-out-str (pprint/pprint req))
             :headers {"Content-Type" "application/json"}}
    "/greeting" {:status 200
                 :body "Hello, World!"
                 :headers {"Content-Type" "text/plain"}}
    {:status 404
     :body "Not Found."
     :headers {"Content-Type" "text-plain"}}))

(c/defroutes routes
             (c/GET "/" [] {:status 200
                            :body "<h1>Homepage</h1>
                                  <ul>
                                    <li><a href=\"/echo\">Echo request</a></li>
                                    <li><a href=\"/greeting\">Greeting</a></li>
                                  </ul"
                            :headers {"Content-Type" "text/html; charset=UTF-8"}})
             (c/ANY "/echo" req {:status  200
                                 :body    (with-out-str (pprint/pprint req))
                                 :headers {"Content-Type" "application/json"}})
             (c/GET "/greeting" [] {:status 200
                                   :body "Hello, World!"
                                   :headers {"Content-Type" "text/plain"}})
             (route/not-found {:status 404
                               :body "Not Found."
                               :headers {"Content-Type" "text-plain"}}))

(def app
  (-> (fn [req] (routes req))
      wrap-keyword-params
      wrap-params))

(defn start-server []
  (swap! server
         assoc
         :jetty
          (jetty/run-jetty (fn [req] (app req))
                           {:port  3001
                            :join? false})))

(defn stop-server []
  (when-some [s @server]                                    ;; check if there is an obj int the atom
    (.stop s)                                               ;; call .stop method
    (reset! server nil)))                                   ;; overwrite the atom to nil

(comment
  (start-server)
  (stop-server)

  ,)