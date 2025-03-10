(ns ^:dev/always ; recompile Electric entrypoint when Electric sources change
  user-main
  #?(:cljs (:require-macros user-main))
  (:require contrib.uri ; data_readers
            contrib.ednish
            [hyperfiddle.api :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.router :as router]
            #?(:cljs [hyperfiddle.router-html5 :as html5])
            [user.demo-index :as demos]

            user.demo-1-hello-world
            user.demo-2-toggle
            user.demo-3-system-properties
            user.demo-4-chat
            user.demo-4-chat-extended
            user.demo-4-webview
            user.demo-5-todomvc
            user.demo-todomvc-composed
            user.demo-6-two-clocks
            wip.demo-explorer
            wip.demo-explorer2
            user.demo-10k-dom-elements
            user.todos-simple
            user.seven-gui-1-counter
            user.seven-gui-2-temperature-converter
            user.seven-gui-4-timer
            user.seven-gui-5-crud
            user.demo-scrollview
            user.demo-color
            user.tic-tac-toe
            wip.demo-branched-route
            #_wip.hfql
            wip.tag-picker
            wip.teeshirt-orders

            ; this demo require `npm install`
            #_user.demo-reagent-interop

            ; these demos require extra deps alias
            #_wip.dennis-exception-leak
            #_wip.demo-stage-ui4
            wip.datomic-browser
            ))

#?(:cljs (defn set-page-title! [route]
           (set! (.-title js/document)
                 (str (clojure.string/capitalize (name (first route))) " - Hyperfiddle"))))

(e/defn NotFoundPage []
  (e/client (dom/h1 (dom/text "Page not found"))))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?

(e/defn Pages [page]
  (e/server
    (case page
      `user.demo-index/Demos user.demo-index/Demos
      `user.demo-index/Secrets user.demo-index/Secrets
      `user.demo-1-hello-world/HelloWorld user.demo-1-hello-world/HelloWorld
      ::demos/hfql-teeshirt-orders wip.teeshirt-orders/App
      `wip.demo-explorer/DirectoryExplorer wip.demo-explorer/DirectoryExplorer
      ::demos/explorer2 wip.demo-explorer2/App
      ;::demos/demo-10k-dom-elements user.demo-10k-dom-elements/App ; todo too slow to unmount, crashes
      ::demos/router-recursion wip.demo-branched-route/App
      ::demos/tag-picker wip.tag-picker/App
      ::demos/toggle user.demo-2-toggle/App
      ::demos/system-properties user.demo-3-system-properties/App
      ::demos/chat user.demo-4-chat/App
      ::demos/chat-extended user.demo-4-chat-extended/App
      ::demos/webview user.demo-4-webview/App
      ::demos/todos-simple user.todos-simple/Todo-list ; css fixes
      ::demos/todomvc user.demo-5-todomvc/App
      ::demos/todomvc-composed user.demo-todomvc-composed/App
      ::demos/color user.demo-color/App
      ::demos/two-clocks user.demo-6-two-clocks/App
      ::demos/infinite-scroll user.demo-scrollview/Demo
      ::demos/seven-guis-counter user.seven-gui-1-counter/Counter
      ::demos/seven-guis-temperature-converter user.seven-gui-2-temperature-converter/App
      ::demos/seven-guis-timer user.seven-gui-4-timer/Timer
      ::demos/seven-guis-crud user.seven-gui-5-crud/App
      ::demos/tic-tac-toe user.tic-tac-toe/App
      ;`user.demo-reagent-interop/ReagentInterop (when react-available user.demo-reagent-interop/ReagentInterop)
      ;::demos/dennis-exception-leak wip.dennis-exception-leak/App2
      ;::demos/demo-stage-ui4 wip.demo-stage-ui4/Demo
      ;`wip.datomic-browser/DatomicBrowser wip.datomic-browser/DatomicBrowser
      NotFoundPage)))

(e/defn Main []
  (binding [router/encode contrib.ednish/encode-uri
            router/decode #(or (contrib.ednish/decode-path % hf/read-edn-str)
                               [`user.demo-index/Demos])]
    (router/router (html5/HTML5-History.)
      (set-page-title! router/route)
      (binding [dom/node js/document.body]
        (dom/pre (dom/text (contrib.str/pprint-str router/route)))
        (let [[page & args] router/route]
          (e/server (new (Pages. page #_args))))))))
