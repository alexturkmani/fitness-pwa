/**
 * Renders a JSON-LD block. `<` is escaped as < so text containing
 * `</script>` can never close the tag early; the JSON stays equivalent.
 */
export function serializeJsonLd(data: unknown): string {
  return JSON.stringify(data).replace(/</g, '\\u003c');
}

export default function JsonLd({ data }: { data: unknown }) {
  return <script type="application/ld+json" dangerouslySetInnerHTML={{ __html: serializeJsonLd(data) }} />;
}
