import type { Metadata } from 'next';
import SeoLanding from '../../components/SeoLanding';
import { seoPageMetadata } from '../../site-config';

const page = {
  path: '/compare/myfitnesspal-alternative',
  title: 'MyFitnessPal Alternative for Android: Nexal',
  description: 'Compare Nexal and MyFitnessPal on Android: free macro logging, ads, barcode scanning, workouts and AI planning, with the trade-offs stated plainly.',
};

export const metadata: Metadata = seoPageMetadata(page.path, page.title, page.description);

export default function Page() {
  return <SeoLanding
    page={page}
    slug="compare_myfitnesspal"
    kicker="AN HONEST COMPARISON"
    title="A MyFitnessPal alternative for Android that adds your workouts"
    intro="MyFitnessPal is an established food tracker with a very large food database. Nexal is a smaller Android app that keeps free calorie and macro logging beside your workouts, with no ads. Here is where each one fits, including where MyFitnessPal is the stronger choice."
    sectionTitle="Why people look at Nexal instead"
    guideTopic="CHOOSING AN APP"
    preview={{
      mode: 'tabs',
      label: 'ONE DAY IN NEXAL',
      tabs: [
        { id: 'nutrition', label: 'Nutrition', heading: 'Food and training, one view.', rows: [
          { badge: 'AM', type: 'BREAKFAST', name: 'Oats, milk and banana', detail: '420 kcal' },
          { badge: 'PM', type: 'LUNCH', name: 'Chicken and rice bowl', detail: '610 kcal' },
          { badge: 'P', type: 'PROTEIN', name: '67 of 150 g today', detail: '45%' },
        ] },
        { id: 'training', label: 'Training', heading: 'Training and food, one view.', rows: [
          { badge: 'MON', type: 'STRENGTH', name: 'Upper body', detail: '32 min' },
          { badge: 'S1', type: 'BENCH PRESS', name: '60 kg × 8', detail: 'Done' },
          { badge: '90s', type: 'REST TIMER', name: 'Next set ready', detail: 'Go' },
        ] },
      ],
    }}
    benefits={[
      { title: 'Workouts and food together', text: 'Log meals and sets in the same app and see the day as a whole, instead of keeping a food tracker and a separate gym log.' },
      { title: 'No ads on free logging', text: 'Nexal does not show advertising on any plan. Free calorie and macro logging has no ad breaks and no trial clock.' },
      { title: 'A Premium you can explain', text: 'Premium adds AI workout and meal plans, AI macro estimates and barcode scanning. Core logging never moves behind it.' },
    ]}
    comparison={{
      heading: 'Nexal vs MyFitnessPal, feature by feature',
      intro: 'Both apps are free to download. The differences are in how food gets into the log, what the free plan includes and whether training sits beside nutrition.',
      competitor: 'MyFitnessPal',
      checked: '2026-11-14',
      sources: [{ label: 'MyFitnessPal Premium', href: 'https://www.myfitnesspal.com/premium' }],
      rows: [
        { feature: 'Calorie and macro logging', nexal: 'Free: calories, protein, carbohydrates and fat', other: 'Free food, nutrition and weight tracking' },
        { feature: 'Ads on the free plan', nexal: 'None, on any plan', other: 'Shown; ad-free logging is a Premium feature' },
        { feature: 'How food is entered', nexal: 'Manual entry and recent foods; AI estimates with Premium', other: 'Search a very large food database' },
        { feature: 'Barcode scanning', nexal: 'Premium', other: 'Premium' },
        { feature: 'Macro targets', nexal: 'Calculated from your profile for free; custom gram targets not supported', other: 'Custom goals by gram or percentage are Premium' },
        { feature: 'Workout logging', nexal: 'Free: sets, reps, weight, rest timer, history and weekly volume', other: 'Free exercise tracking' },
        { feature: 'AI workout and meal plans', nexal: 'Premium: 4 to 6 week workout plans and macro-based meal plans', other: 'Check the current listing for meal-planning tiers' },
        { feature: 'Intermittent fasting timer', nexal: 'Free', other: 'Premium' },
        { feature: 'Platforms and integrations', nexal: 'Android phone only; no wearable or Health Connect sync', other: 'Android, iPhone and web, with many app and device connections' },
      ],
    }}
    feature={{
      heading: 'Who should stay with MyFitnessPal, and who Nexal suits',
      intro: 'A fair comparison should help you leave a page knowing which app fits your week, even when the answer is not ours.',
      points: [
        { title: 'Stay with MyFitnessPal if', text: 'you search a food database for almost every meal, rely on device integrations, or need the same account on iPhone and the web.' },
        { title: 'Try Nexal if', text: 'you train and want macros next to your workouts, prefer no ads, and are comfortable with manual entry or Premium scanning.' },
        { title: 'Test both on one real week', text: 'Log the same week in each app. The better tracker is the one you still open on day seven, not the longer feature list.' },
      ],
    }}
    sections={[
      { title: 'Logging speed vs logging focus', text: 'A large database makes finding a food fast. Manual entry is slower but keeps you close to the label. Nexal narrows the gap with recent foods and optional AI estimates.' },
      { title: 'Switching: what comes with you', text: 'Nexal does not import MyFitnessPal history. Keep your old account for past records and start Nexal from a fresh week, so the trends you see are consistent.' },
      { title: 'The practical macro difference', text: 'On free MyFitnessPal you see macros, but custom gram goals are Premium. On Nexal, targets come from your profile on the free plan. Neither lets you type custom grams for free.' },
      { title: 'Know the price before you pay', text: 'Nexal Premium prices are shown in Google Play before you subscribe, and the free plan has no trial to cancel. Compare the current prices of both apps in your region.' },
    ]}
    faqs={[
      { q: 'Is Nexal a free alternative to MyFitnessPal?', a: 'For core logging, yes. Calories, protein, carbohydrates, fat, water and workouts are free in Nexal with no ads. MyFitnessPal has a much larger food database, so the better fit depends on how you log.' },
      { q: 'Does Nexal have a barcode scanner like MyFitnessPal?', a: 'Yes, as a Premium feature. MyFitnessPal also lists barcode scanning as Premium.' },
      { q: 'Can I import my MyFitnessPal data into Nexal?', a: 'Not currently. Keep your MyFitnessPal account for past records and start fresh in Nexal.' },
      { q: 'Does Nexal have ads like the free MyFitnessPal plan?', a: 'No. Nexal does not show ads on the free plan or on Premium.' },
      { q: 'Is Nexal available on iPhone?', a: 'No. Nexal is currently available for Android through Google Play. MyFitnessPal is available on Android, iPhone and the web.' },
    ]}
  />;
}
