// access the pre-bundled global API functions
const invoke = window.__TAURI__.invoke

// now we can call our Command!
// Right-click the application background and open the developer tools.
// You will see "Hello, World!" printed in the console!
invoke('greet', { name: 'World' })
// `invoke` returns a Promise
.then((response) => console.log(response))

// const Command = window.__TAURI__.shell.Command
const command = window.__TAURI__.shell.Command.sidecar('binaries/bb', ['/Users/philippkueng/Downloads/http-server.clj'])
command.on('close', data => {
  console.log(`command finished with code ${data.code} and signal ${data.signal}`)
});
command.on('error', error => console.error(`command error: "${error}"`));
command.stdout.on('data', line => console.log(`command stdout: "${line}"`));
command.stderr.on('data', line => console.log(`command stderr: "${line}"`));

command.execute();
//console.log('pid:', child.pid);


//const output = await command.execute()