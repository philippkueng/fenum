# fenum

A local simplified Kibana

## tasks

- read lists of nested maps and turn those into maps without nesting
- generate a schema from a given list of maps -> so datalite can generate tables from it
- option with a backend process
  - using Clojure and compile it to a GraalVM native binary
  - option with a babashka based backend (not sure if I can include websocket support)
- option with talking to SQLite and persisting the data into IndexedDB using https://github.com/jlongster/absurd-sql

## development

```bash
cd backend/
npm run tauri dev
```

```bash
#cd fenum/
#npx shadow-cljs browser-repl
cd shadow-cljs-tailwindcss/
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
cd backend/
npm run tauri build
```