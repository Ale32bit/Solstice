---
hide:
  - navigation
---

# Text formatting

Solstice makes use of the [Simplified Text Format](https://placeholders.pb4.eu/user/text-format/) to format text messages present in `locale.json`, some of the configurations and in many features.

[Placeholders](https://placeholders.pb4.eu/user/default-placeholders/) can also be used to dynamically replace text based on the context.

## Extensions

Solstice introduces new tags to be used within the mod itself.

### Phase Gradient

This tag improves upon Placeholder's gradient tags to fix its issues and implement the phase argument to shift the gradient.

The tag accepts any amount of colors in its arguments and the phase argument, a decimal number between -1.0 and 1.0, can be used to shift the gradient around.

* `#!xml <phase_gradient:[colors 1]:[color 2]:...:[phase]>`

This tag can be used from its aliases `<pgr>` and `<sgr>`.

Just like other Placeholder gradients, this tag requires static text.