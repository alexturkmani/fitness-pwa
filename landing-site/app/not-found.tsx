import Link from 'next/link';
import { BrandMark, SiteFooter } from './components/MarketingUi';

export default function NotFound() {
  return <main><nav className="nav shell"><Link href="/" aria-label="Nexal home"><BrandMark /></Link></nav><section className="shell not-found"><span className="section-kicker">404: PAGE NOT FOUND</span><h1>Let’s get you back on track.</h1><p>This page does not exist. Explore the fitness guides or return to the Nexal homepage.</p><Link className="text-link" href="/guides">Explore fitness guides →</Link><p><Link href="/">Return to Nexal home</Link></p></section><SiteFooter /></main>;
}
