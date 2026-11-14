import type { Metadata } from 'next';
import SeoLanding from '../components/SeoLanding';
import { seoPageMetadata } from '../site-config';

const page = {
  path: '/free-calorie-and-macro-tracker',
  title: 'Free Calorie & Macro Tracker App for Android',
  description: 'Log calories, protein, carbs and fat for free in Nexal for Android, with no ads on core logging. Barcode scanning and AI estimates are optional Premium.',
};

export const metadata: Metadata = seoPageMetadata(page.path, page.title, page.description);

export default function Page() {
  return <SeoLanding
    page={page}
    slug="free_calorie_macro_tracker"
    kicker="FREE CORE LOGGING. NO ADS."
    title="Free calorie and macro tracker app for Android"
    intro="Log meals and see calories, protein, carbohydrates and fat against daily targets set from your profile. Core food logging in Nexal is free, with no ads and no trial clock. Premium adds AI macro estimates and a barcode scanner when you want faster entry."
    sectionTitle="What you get without paying"
    guideTopic="NUTRITION TRACKING"
    preview={{
      mode: 'meals',
      label: 'TODAY, ON THE FREE PLAN',
      heading: 'Tap a meal to log it.',
      targets: { kcal: 2100, protein: 150, carbs: 230, fat: 70 },
      initiallyLogged: [0],
      meals: [
        { badge: 'AM', type: 'BREAKFAST', name: 'Oats, milk and banana', kcal: 420, protein: 22, carbs: 68, fat: 9 },
        { badge: 'PM', type: 'LUNCH', name: 'Chicken and rice bowl', kcal: 610, protein: 45, carbs: 70, fat: 14 },
        { badge: '4P', type: 'SNACK', name: 'Greek yoghurt', kcal: 150, protein: 15, carbs: 12, fat: 4 },
      ],
    }}
    benefits={[
      { title: 'The core log is free', text: 'Meal entries, calories, protein, carbohydrates, fat, water and recent foods work without a subscription or a trial that runs out.' },
      { title: 'Targets from your profile', text: 'Nexal works out daily calorie and macro targets from your goal, body weight and activity level, so the numbers have context from day one.' },
      { title: 'No ads in the app', text: 'Nexal does not show advertising on the free plan or on Premium. Logging a meal is not interrupted by a sponsored banner.' },
    ]}
    feature={{
      heading: 'Exactly what is free, and what Premium adds',
      intro: 'A free tracker is only useful if you know where the line is before you build a habit around it. This is the line in Nexal today.',
      points: [
        { title: 'Free: manual food logging', text: 'Enter a food name, serving, calories and macros from the label or a source you trust. Re-log recent foods in a tap. Nexal does not include a searchable food database.' },
        { title: 'Free: targets, water and history', text: 'See each day against your calorie, protein, carbohydrate and fat targets, log water against a daily goal and look back at previous days.' },
        { title: 'Premium: faster entry', text: 'Premium adds a barcode scanner for packaged foods, AI macro estimates as you type a meal, and AI meal plans. Prices are shown in Google Play before you subscribe.' },
      ],
    }}
    sections={[
      { title: 'Manual entry, stated plainly', text: 'Free logging means typing the values yourself. That is slower than scanning a barcode, but it keeps you close to the numbers, and foods you eat often appear in recent foods so you do not type them twice.' },
      { title: 'Why macros, not only calories', text: 'Two days with the same calories can look very different in protein. Seeing protein, carbohydrates and fat next to the calorie total helps you notice patterns, such as protein that is consistently low on training days.' },
      { title: 'Treat every number as an estimate', text: 'Labels, recipes and serving sizes all carry error. Use the daily totals to spot trends over weeks rather than chasing an exact figure for a single meal.' },
      { title: 'When Premium is worth considering', text: 'If you eat a lot of packaged food, the barcode scanner saves time. If you cook mixed meals, AI estimates can give you a starting point to check. If manual entry already works for you, the free plan is enough.' },
    ]}
    faqs={[
      { q: 'Is the Nexal calorie and macro tracker really free?', a: 'Yes. Logging meals, calories, protein, carbohydrates, fat and water is free with no time limit. Premium is optional and adds AI planning, AI macro estimates and barcode scanning.' },
      { q: 'Does the free version track protein, carbs and fat?', a: 'Yes. Every entry records calories, protein, carbohydrates and fat, and each day shows your totals against targets calculated from your profile.' },
      { q: 'Is there a barcode scanner in the free version?', a: 'No. The barcode scanner is a Premium feature. On the free plan you enter values manually and can re-log recent foods quickly.' },
      { q: 'Does Nexal show ads?', a: 'No. Nexal does not show ads on the free plan or on Premium.' },
      { q: 'Can I set my own macro targets?', a: 'Nexal calculates calorie and macro targets from your goal, weight and activity level. Update those details to change the targets. Typing custom gram targets is not currently supported.' },
    ]}
  />;
}
