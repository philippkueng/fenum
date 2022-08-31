#![cfg_attr(
  all(not(debug_assertions), target_os = "windows"),
  windows_subsystem = "windows"
)]

use tauri::{
  api::process::{Command, CommandEvent},
  Manager,
};

// #[tauri::command]
// fn greet(name: &str) -> String {
//   format!("Hello, {}!", name)
// }

// fn main() {
//   let (mut rx, mut child) = Command::new_sidecar("background-process")
//       .expect("failed to create `background-process` binary command")
//       .spawn()
//       .expect("Failed to spawn sidecar");
//
//   tauri::async_runtime::spawn(async move {
//     // read events such as stdout
//     while let Some(event) = rx.recv().await {
//       if let CommandEvent::Stdout(line) = event {
//         window
//             .emit("message", Some(format!("'{}'", line)))
//             .expect("failed to emit event");
//         // write to stdin
//         child.write("message from Rust\n".as_bytes()).unwrap();
//       }
//     }
//   });
//
//   tauri::Builder::default()
//       .invoke_handler(tauri::generate_handler![greet])
//       .run(tauri::generate_context!())
//       .expect("error while running tauri application");
// }

fn main() {
  tauri::Builder::default()
      .setup(|app| {
        let window = app.get_window("main").unwrap();
        tauri::async_runtime::spawn(async move {
          let (mut rx, mut child) = Command::new_sidecar("bb")
              .expect("failed to setup `bb` sidecar")
              .spawn()
              .expect("Failed to spawn packaged node");

          let mut i = 0;
          while let Some(event) = rx.recv().await {
            if let CommandEvent::Stdout(line) = event {
              window
                  .emit("message", Some(format!("'{}'", line)))
                  .expect("failed to emit event");
              i += 1;
              if i == 4 {
                child.write("message from Rust\n".as_bytes()).unwrap();
                i = 0;
              }
            }
          }
        });

        Ok(())
      })
      .run(tauri::generate_context!())
      .expect("error while running tauri application");
}