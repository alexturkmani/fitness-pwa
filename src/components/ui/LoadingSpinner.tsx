export default function LoadingSpinner({ message = 'Loading...' }: { message?: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-4">
      <div className="w-12 h-12 border-4 border-dark-800 border-t-primary-500 rounded-full spinner" />
      <p className="text-dark-400 text-sm">{message}</p>
    </div>
  );
}
