(ns readerss.file-system)

(defn is-file?
  [file]
  (and file
       (.exists file)
       (.isFile file)))