<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->

# Errors

This Beam Go SDK `errors` package is intended to be used for all errors
internally within the Go SDK. In order to maintain consistency, the
functionality in this package should be used instead of standard Go error
handling functionality or other custom error packages. To maintain a consistent
style, the style guide in this readme should be followed by all errors within
the Go SDK.

## Style Guide

### Wrapping

Wrapping an error means adding additional messages to an already existing error.
In standard Go this is often done by appending an existing error message to a
new string. For example, this is a common pattern:

```
if err != nil {
    return _, fmt.Errorsf("additional context: %v", err)
}
```

This `errors` package adds functionality to handle wrapping, which is
incompatible with standard approaches such as the example above. All wrapping in
the Go SDK must be done with the functions `Wrap`, `Wrapf`, `WithContext`, or
`WithContextf` from this package. The example above should look like this:

```
if err != nil {
    return _, errors.WithContext(err, "additional context")
}
```

### Context Messages

Context messages are distinct from error messages in that they describe what is
happening when an error occurs. Context messages should therefore be in the
present continuous tense ("doing an action"), such as in the following examples:

> opening file 'example.txt'
> serializing ParDo
> validating inputs to transform "UserParDo"

Context messages should describe the current function, **not** functions being
called.

```
func DoingFoo() error {
    ...
    err := DoingBar()
    if err != nil {
        // BAD - this does not describe the current function.
        return errors.WithContext(err, "doing Bar")

        // GOOD
        return errors.WithContext(err, "doing Foo")
    }
}
```

Context messages do not need to be added at every function call like a stack
trace. Instead, add them only for important actions or to print useful variables
that aren't visible in the function emitting the error.

```
func DoingFoo(s Scope) error {
    ...
    err := DoingFooImpl()
    if err != nil {
        // DoingFooImpl can't see the scope, so we should output it here.
        return errors.WithContextf(err, "doing Foo in scope %s", s)
    }
}
```


### Error Messages

There are no unique rules for error messages in Beam. Error messages should
still follow the Go standard of being all lowercase, not ending with periods,
and avoiding newlines. Otherwise, they can be formatted any way that provides
enough description of the error. Some examples:

> failed to open file 'example.txt'
> could not bind inputs: expected 2 inputs, got 3
> unexpected value 10 for foo: foo must be less than 5

Error messages can be wrapped to indicate that one error is caused by another.
However, wrapping error messages should be done sparingly as the same
information can usually be conveyed by adding context. It is always preferable
to wrap with context instead of errors.

```
// BAD - This could easily be context. 
return errors.Wrap(err, "could not do Foo")

// GOOD
return errors.Wrap(err, "doing Foo")
```

Wrapping with errors is advised if an action can fail with a wide variety of
error messages, or if the error messages are too low level to make sense. Some
examples of cases where wrapping with an error is advised:

* Failing to connect to a server: There are a large variety of possible
  underlying errors, many of which often don't provide much information.
* Writing a file: This may fail for low level reasons that are disconnected from
  the actual logic, such as the OS running out of memory.
* Calling a third party package: Wrapping allows the third party error to be
  easily distinguished from Beam code.

### Top Level Errors

Top level errors are meant to be aimed specifically at end-users and should
explain in broad details what went wrong and how to fix it. These messages
should not use any terminology specific to internal details, nor mention any
implementation details that end users have little control over. 
