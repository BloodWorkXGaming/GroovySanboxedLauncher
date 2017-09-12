# GroovySandboxLauncher

## TODO
- ? Possibly language transformers
- way of enabling debug info          (preprocessor is there, does nothing just yet)
- ? per file toggleable debug info    (^)
- ? possibly loader names             (^)
- only print stacktraces when debug is enabled
- Events that get called on the different stages

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
- preprocessors, custom preprocessors can be registered
  - load order (priority preprocessor)
- configurable binding (setter/getter)
