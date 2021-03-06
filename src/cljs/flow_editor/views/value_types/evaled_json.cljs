(ns flow-editor.views.value-types.evaled-json
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            [flow-editor.views.utils.codemirror :refer [cm]]
            [re-com.core :refer [title
                                 horizontal-bar-tabs
                                 label
                                 md-icon-button
                                 button
                                 v-box
                                 h-box
                                 box
                                 single-dropdown
                                 h-split]]))



(defn eval
  [json]
  (.parse js/JSON json))


(defn json
  [obj]
  (.stringify js/JSON obj nil "  "))


(defn value-editor
  [eid current-value]
  (let [editing (r/atom false)]
    (fn [eid current-value]
      (if @editing
        (let [changes (atom (json (:value current-value)))]
          (dispatch [:flow-runtime/unwatch-entity eid])
          [v-box
           :gap "5px"
           :children [[cm @changes {:mode "javascript"} changes]
                      [h-box
                       :gap "10px"
                       :children [[button
                                   :label "set"
                                   :class "btn-primary"
                                   :on-click #(do (dispatch [:flow-runtime/set-current-value
                                                             eid (eval @changes)])
                                                  (reset! editing false))]
                                  [button
                                   :label "cancel"
                                   :on-click #(reset! editing false)]]]]])
        (do (dispatch [:flow-runtime/watch-entity eid])
            [v-box
             :gap "5px"
             :children [[:pre
                         {:on-click #(reset! editing true)}
                         (json (:value current-value))]
                        [button
                         :label "edit"
                         :on-click #(reset! editing true)]]])))))
