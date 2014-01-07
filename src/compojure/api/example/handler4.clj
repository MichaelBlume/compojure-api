(ns compojure.api.example.handler4
  (:require [compojure.core :refer :all]
            [compojure.api.core :refer [defapi with-middleware public-resources]]
            [compojure.api.swagger :refer :all]
            [compojure.api.common :refer :all]
            [compojure.api.example.domain :refer :all]
            [compojure.api.dsl :refer :all]))

(defapi app
  (with-middleware [public-resources]
    (swagger-docs "/api/docs"
      :title "Cool api"
      :description "Compojure Sample Web Api")
    (swaggered :sample
      :description "sample api"
      (context "/api" []
        (GET* "/pizzas" []
          :return   Pizza
          :summary  "Gets all Pizzas"
          :nickname "getPizzas"
          (ok (get-pizzas)))
        (GET* "/pizzas/:id" [id]
          :return   Pizza
          :summary  "Gets a pizza"
          :nickname "getPizza"
          (ok (get-pizza (->Long id))))
        (POST* "/pizzas" []
          :return   Pizza
          :body     [pizza NewPizza {:description "new pizza"}]
          :summary  "Adds a pizza"
          :nickname "addPizza"
          (ok (add! pizza)))
        (PUT* "/pizzas" []
          :return   Pizza
          :body     [pizza Pizza]
          :summary  "Updates a pizza"
          :nickname "updatePizza"
          (ok (update! pizza)))
        (DELETE* "/pizzas/:id" [id]
          :return   Pizza
          :summary  "Deletes a Pizza"
          :nickname "deletePizza"
          (ok (delete! (->Long id))))))))