(ns conduit.views.editor
  (:require [conduit.views.components :refer [large-input textarea input error-list]]
            [re-frame.core :as re-frame]
            [dommy.core :refer-macros [sel1]]
            [cljsjs.react-select]
            [reagent.core :as r]))

(defn page []
  (re-frame/dispatch [:get-tags])
  (let [tags (re-frame/subscribe [:tags])
        selected-tags (r/atom #{})
        create-article-pending (re-frame/subscribe [:request-pending :create-article!])
        create-article-errors (re-frame/subscribe [:request-errors :create-article!])]
    (fn []
      [:div.editor-page
       [:div.container.page
        [:div.row
         [:div.col-md-10.offset-md-1.col-xs-12
          [error-list @create-article-errors]
          [:form
           [:fieldset
            [large-input {:id "title" :placeholder "Article Title"}]
            [input {:id "description" :placeholder "What's this article about?"}]
            [textarea {:id "body" :placeholder "Write your article (in markdown)"}]
            [:fieldset.form-group
             [:> js/Select.Creatable
              {:placeholder       "Enter tags"
               :promptTextCreator #(str "Create \"" % "\" tag?")
               :name              "tags-select"
               :multi             true
               :options           (clj->js (map #(do {:value % :label %}) @tags))
               :value             @selected-tags
               :on-change         (fn [selected]
                                    (->> selected
                                         (map #(.-value %))
                                         (reset! selected-tags)))}]]
            [:button.btn.btn-lg.pull-xs-right.btn-primary
             {:type     :button
              :class    (when @create-article-pending "disabled loading")
              :on-click #(re-frame/dispatch [:create-article!
                                             {:title       (.-value (sel1 :#title))
                                              :description (.-value (sel1 :#description))
                                              :body        (.-value (sel1 :#body))
                                              :tagList     @selected-tags}])}
             "Publish Article"]]]]]]])))