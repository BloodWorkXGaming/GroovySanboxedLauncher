# GroovySandboxLauncher

## TODO
- Possibly language transformers
- configurable binding
- load order
- ? preprocessors
- ? possibly loader names
- way of enabling debug info 
- ? per file toggleable debug info
- only print stacktraces when debug is enabled

## What it already can do
- Launch any the script inside a sandbox
- configure custom imports
- configure allowed
  - function calls
  - field calls
  - constructor calls
  - objects to exist
- invertible whitelist -> blacklist
- Annotations
- Add mixins
- custom CompilerCustomizers
- block super calls to not allowed classes, as that would allow bypassing anything.
