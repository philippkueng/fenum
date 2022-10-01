# 🪡 fenum

A local simplified Kibana

## Mission for this project

Currently this project is a mess and will most likely be one for the foreseeable future. My ambition is to use it as a test-bed for [bitfondue](https://bitfondue.com) for which I'm looking for the following qualities: speed and simplicity of expressing UI logic and workflows to allow me to figure out what kind of interactions with the underlying data make sense.  In order to reach those ambitions I'm betting on a datalog based query engine however paired with the ubiquitous SQLite as the storage format ([datalite](https://github.com/philippkueng/datalite) - also very experimental) as it might allow for porting the logic and data from desktop apps across operating systems and device boundaries.

## 📄 Tasks

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
- next steps
  - add honeysql and datalite as a `frontend` dependency and let the user enter some edn data and then extract the schema from it and create the tables and insert the data
  - when the homescreen is initially loaded we should query the SQLite database for the available tables and show some rows
  - create a babashka script to take a folder of JSON files or a single JSON file and turn it into a SQLite database that can be used by fenum
  - add babashka again and make it call `.clj` files both in development and when having a production build
  - [ ] configure the tauri installer to not need admin permissions any longer using https://github.com/tauri-apps/tauri/issues/2319

## 🧑‍💻 Development

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