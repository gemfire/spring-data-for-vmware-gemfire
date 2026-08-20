Type: grilling
Status: resolved

## Question

Should the SPI itself impose or document thread-safety guarantees for its interfaces (e.g. requiring implementations to be safe for concurrent use), independent of what the underlying Driver Impl actually provides?

## Answer

**No** — thread-safety is impl-defined and undocumented by the SPI itself. This reflects a general architectural stance, not just a one-off answer for this question: **the SPI is a thin wrapper/delegate around the Driver Impl**. It imposes no contract, guarantee, or requirement beyond what the underlying Driver Impl naturally provides. If a Driver Impl doesn't guarantee thread-safety for an operation, the SPI doesn't manufacture that guarantee either — it passes the underlying behavior through unchanged.

This "thin delegate" principle should be applied as a default lens for any other SPI design question in the spec where the SPI could otherwise be tempted to add behavior, guarantees, or contracts the Driver Impl doesn't itself provide.
