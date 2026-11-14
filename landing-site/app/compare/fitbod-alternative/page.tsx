import type { Metadata } from 'next';
import SeoLanding from '../../components/SeoLanding';
import { seoPageMetadata } from '../../site-config';

const page = {
  path: '/compare/fitbod-alternative',
  title: 'Fitbod Alternative with Meal Planning: Nexal',
  description: 'Compare Nexal and Fitbod on Android: AI workout plans, meal and macro planning, free logging and watch support, with each app’s strengths stated plainly.',
};

export const metadata: Metadata = seoPageMetadata(page.path, page.title, page.description);

export default function Page() {
  return <SeoLanding
    page={page}
    slug="compare_fitbod"
    kicker="AN HONEST COMPARISON"
    title="A Fitbod alternative for Android with meal planning"
    intro="Fitbod is a focused strength app that adjusts each workout to your logged sets and recovery. Nexal is an Android app that pairs AI workout plans with AI meal plans and free calorie and macro logging. If nutrition is part of your plan, here is how they compare."
    sectionTitle="Why people look at Nexal instead"
    guideTopic="CHOOSING AN APP"
    preview={{
      mode: 'tabs',
      label: 'YOUR WEEK IN NEXAL',
      tabs: [
        { id: 'workouts', label: 'Workouts', heading: 'Plan both sides of the week.', rows: [
          { badge: 'MON', type: 'STRENGTH', name: 'Upper body', detail: '32 min' },
          { badge: 'WED', type: 'STRENGTH', name: 'Lower body', detail: '40 min' },
          { badge: 'FRI', type: 'FULL BODY', name: 'Squat, press and row', detail: '35 min' },
        ] },
        { id: 'meals', label: 'Meals', heading: 'Plan both sides of the week.', rows: [
          { badge: 'AM', type: 'BREAKFAST', name: 'Protein oat bowl', detail: '420 kcal' },
          { badge: 'PM', type: 'LUNCH', name: 'Chicken and rice bowl', detail: '610 kcal' },
          { badge: 'EVE', type: 'DINNER', name: 'Salmon, potatoes, greens', detail: '540 kcal' },
        ] },
      ],
    }}
    benefits={[
      { title: 'Training and meals, one plan', text: 'Nexal Premium generates a workout plan and a meal plan built around your calorie and macro targets, so you are not stitching two apps together.' },
      { title: 'Free logging, no time limit', text: 'Workout and food logging stay free after any trial would have ended. Premium is only needed for the AI planning tools.' },
      { title: 'Built only for Android', text: 'Nexal is designed and tested as an Android phone app first, not adapted from an iPhone version.' },
    ]}
    comparison={{
      heading: 'Nexal vs Fitbod, feature by feature',
      intro: 'Fitbod goes deeper on adaptive strength programming. Nexal covers nutrition as well as training. The table shows where each app is stronger.',
      competitor: 'Fitbod',
      checked: '2026-11-14',
      sources: [
        { label: 'Fitbod system requirements', href: 'https://help.fitbod.me/hc/en-us/articles/38144798013719-System-Requirements' },
        { label: 'Fitbod on Wear OS', href: 'https://help.fitbod.me/hc/en-us/articles/4406074415383-WearOS-on-Android' },
      ],
      rows: [
        { feature: 'Free use', nexal: 'Workout and food logging free with no time limit', other: 'Limited free trial, then a subscription' },
        { feature: 'AI workout programming', nexal: 'Premium: 4 to 6 week plans from your goal, experience and 3 to 7 training days', other: 'Core feature: each workout is generated from your logs and muscle recovery' },
        { feature: 'Adjusts after every session', nexal: 'No; the plan is generated up front and can be regenerated', other: 'Yes' },
        { feature: 'Meal planning', nexal: 'Premium: AI meal plans from calorie and macro targets, preferences and allergies', other: 'Not offered in the app' },
        { feature: 'Calorie and macro logging', nexal: 'Free', other: 'Not offered in the app' },
        { feature: 'Workout logging and history', nexal: 'Free: sets, reps, weight, rest timer and weekly volume', other: 'Detailed logging and progress views with a subscription' },
        { feature: 'Platforms and watches', nexal: 'Android phone only; no watch app', other: 'iPhone and Android, with Apple Watch and Wear OS apps' },
      ],
    }}
    feature={{
      heading: 'Who should stay with Fitbod, and who Nexal suits',
      intro: 'Fitbod is a strong choice for people who want the app to decide each strength session. Nexal is for people who want training and food planned together.',
      points: [
        { title: 'Stay with Fitbod if', text: 'you want workouts recalculated after every session, train mostly for strength and hypertrophy, or log from a smartwatch.' },
        { title: 'Try Nexal if', text: 'you want meal plans and macro tracking next to your training, prefer free logging, and plan in four to six week blocks.' },
        { title: 'Or use both', text: 'Some people keep Fitbod for sessions and use free Nexal logging for food. Choose what you will maintain, not the most features.' },
      ],
    }}
    sections={[
      { title: 'Adaptive sessions vs a planned block', text: 'Fitbod chooses each workout from your recent training. Nexal Premium plans a four to six week block up front. Some people prefer the variety; others prefer knowing next week in advance.' },
      { title: 'Why nutrition changes the comparison', text: 'Training sets the stimulus; food and recovery shape what you get from it. If you already track food elsewhere, a workout-only app may be all you need. If not, one app can be simpler.' },
      { title: 'Watch users should weigh this', text: 'Fitbod supports Apple Watch and Wear OS. Nexal has no watch app and does not sync with Health Connect, so you log from your phone.' },
      { title: 'Count the cost of two apps', text: 'A workout subscription plus a separate nutrition subscription can cost more than one app covering both. Compare current Google Play prices in your region before deciding.' },
    ]}
    faqs={[
      { q: 'Does Fitbod include meal planning or calorie tracking?', a: 'Fitbod focuses on workouts and does not offer meal planning or food logging in the app. Nexal includes free calorie and macro logging and AI meal plans with Premium.' },
      { q: 'Is Nexal free to use?', a: 'Yes. Workout logging and calorie and macro logging are free with no time limit. Premium adds AI workout plans, AI meal plans, AI macro estimates and barcode scanning.' },
      { q: 'Does Nexal adapt every workout like Fitbod?', a: 'No. Nexal Premium generates a four to six week plan up front, which you can regenerate when your goals or schedule change. Fitbod adjusts each session.' },
      { q: 'Does Nexal work on Wear OS or Apple Watch?', a: 'Not currently. Nexal is an Android phone app. Fitbod offers Apple Watch and Wear OS apps.' },
      { q: 'Can I use Nexal for nutrition and keep Fitbod for training?', a: 'Yes. Nexal food logging is free, so you can use it alongside a workout app. You can also log workouts in Nexal later if you want everything in one place.' },
    ]}
  />;
}
