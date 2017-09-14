# GroovySandboxLauncher

## TODO
- ? Possibly language transformers
- way of enabling debug info          (preprocessor is there, does nothing just yet)
- ? per file toggleable debug info    (^)
- ? possibly loader names             (^)
- only print stacktraces when debug is enabled
- Events that get called on the different stages
- block annotations

## What it already can do
- Launch any the script inside a sandbox
- configure custom imports
- configure allowed
  - function calls
  - field calls
  - constructor calls
  - objects to exist
- invertible whitelist -> blacklist
- Annotations to whitelist a class
- Add mixins
- custom CompilerCustomizers
- block super calls to not allowed classes, as that would allow bypassing anything.
- preprocessors, custom preprocessors can be registered
  - load order (priority preprocessor)
  - norun (block execution with preprocessor, script still gets created)
  - nocompile (block script creation)
- configurable binding (setter/getter)
- Optional Parameter (with default values)
- Any Annotation can be replaced by a custom annotation (maybe from a different library)