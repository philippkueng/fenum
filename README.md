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
npm run tauri dev
```

open the Development Console using `CMD + Option + i`

## prepare the binaries

```bash
brew install babashka
cp /usr/local/bin/bb binaries/bb-x86_64-apple-darwin
```

## creating a release

```bash
npm run tauri build
```