# fenum

A local simplified Kibana

## tasks

- read lists of nested maps and turn those into maps without nesting
- generate a schema from a given list of maps -> so datalite can generate tables from it
- option with a backend process
  - using Clojure and compile it to a GraalVM native binary
  - option with a babashka based backend (not sure if I can include websocket & transit support)
    - [ ] package the babashka script with the tauri app
      - if that doesn't work we'll have to generate a single binary, eg. maybe with https://github.com/vercel/pkg
      - or go with a GraalVM binary
    - [ ] assuming the script can be packaged, can we add transit support?
    - [ ] how about if we convert the queries into SQL in the frontend and just send the query/command to the Tauri/Rust handler and let it return the response?
- option with talking to SQLite and persisting the data into IndexedDB using https://github.com/jlongster/absurd-sql
- next steps
  - add honeysql and datalite as a `frontend` dependency and let the user enter some edn data and then extract the schema from it and create the tables and insert the data
  - add re-frame
  - when the homescreen is initially loaded we should query the SQLite database for the available tables and show some rows

## development

```bash
cd backend/
npm run tauri dev
```

```bash
cd frontend/
npx run dev
```

open the Development Console using `CMD + Option + i`

## prepare the binaries

```bash
brew install babashka
cd backend/
cp /usr/local/bin/bb binaries/bb-x86_64-apple-darwin
```

## creating a release

```bash
bb build-release
```

---

When starting the `http-server.clj` from via `babashka` - its serving the contents of the `src-tauri` directory,
this might be the same once we're building and releasing an app.

-----

## Start the backend babashka repl

```bash
cd backend/
#bb repl
bb nrepl-server 1667
```