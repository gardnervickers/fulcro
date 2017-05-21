(ns untangled.client.css-spec
  (:require [untangled-spec.core :refer [specification assertions behavior]]
            [untangled.client.css :as css :refer [localize-classnames]]
            [om.next :as om :refer [defui]]
            [om.dom :as dom]))

(defui Child
  static css/CSS
  (css [this]
    (let [p (css/local-kw Child :p)]
      [p {:font-weight 'bold}]))
  Object
  (render [this]
    (let [{:keys [id label]} (om/props this)]
      (dom/div nil "Hello"))))

(defui Child2
  static css/CSS
  (css [this]
    (let [p  (css/local-kw Child2 :p)
          p2 (css/local-kw Child2 :p2)]
      [[p {:font-weight 'bold}] [p2 {:font-weight 'normal}]]))
  Object
  (render [this]
    (let [{:keys [id label]} (om/props this)]
      (dom/div nil "Hello"))))


(specification "boogers"
  (behavior "can be generated for a class"
    (assertions
      "with a keyword"
      (css/local-class Child :root) => "untangled_client_css-spec_Child__root"
      "with a string"
      (css/local-class Child "root") => "untangled_client_css-spec_Child__root"
      "with a symbol"
      (css/local-class Child 'root) => "untangled_client_css-spec_Child__root")))

(specification "CSS merge"
  (assertions
    "Allows a component to specify a single rule"
    (css/css-merge Child) => [[:.untangled_client_css-spec_Child__p {:font-weight 'bold}]]
    "Allows a component to specify multiple rules"
    (css/css-merge Child2) => [[:.untangled_client_css-spec_Child2__p {:font-weight 'bold}]
                               [:.untangled_client_css-spec_Child2__p2 {:font-weight 'normal}]]
    "Allows component combinations"
    (css/css-merge Child Child2) => [[:.untangled_client_css-spec_Child__p {:font-weight 'bold}]
                                     [:.untangled_client_css-spec_Child2__p {:font-weight 'bold}]
                                     [:.untangled_client_css-spec_Child2__p2 {:font-weight 'normal}]]
    "Merges rules in with component css"
    (css/css-merge Child [:a {:x 1}] Child2) => [[:.untangled_client_css-spec_Child__p {:font-weight 'bold}]
                                                 [:a {:x 1}]
                                                 [:.untangled_client_css-spec_Child2__p {:font-weight 'bold}]
                                                 [:.untangled_client_css-spec_Child2__p2 {:font-weight 'normal}]]))

(defrecord X [])

(defui Boo
  static css/CSS
  (css [this] [:a {:x 1}]))

(specification "apply-css macro"
  (assertions
    "Converts :class entries to localized names for record types"
    (localize-classnames X (pr-str [:a {:b [:c {:d #js {:class :a}}]}])) => #?(:cljs "[:a {:b [:c {:d #js {:className \"untangled_client_css-spec_X__a\"}}]}]"
                                                                               :clj  "[:a {:b [:c {:d {:className \"untangled_client_css-spec_X__a\"}}]}]")
    "Converts :class entries to localized names for defui types"
    (localize-classnames Boo (pr-str [:a {:b [:c {:d #js {:class :a}}]}])) => #?(:cljs "[:a {:b [:c {:d #js {:className \"untangled_client_css-spec_Boo__a\"}}]}]"
                                                                                 :clj  "[:a {:b [:c {:d {:className \"untangled_client_css-spec_Boo__a\"}}]}]")))